package com.deporuis.deporte.aplicacion;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteNoExisteException;
import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import com.deporuis.shared.util.TextoUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeporteService {

    public static final boolean NO_VISIBLE = false;
    @Autowired
    private DeporteRepository deporteRepository;

    @Transactional
    public DeporteResponse crearDeporte(DeporteRequest deporteRequest) {
        String nombreIngresado = deporteRequest.getNombreDeporte();
        String nombreNormalizado = TextoUtil.quitarAcentos(nombreIngresado).toLowerCase();

        List<Deporte> deportes = deporteRepository.findAll();

        Optional<Deporte> deporteExistente = deportes.stream()
                .filter(d -> TextoUtil.quitarAcentos(d.getNombreDeporte()).toLowerCase().equals(nombreNormalizado))
                .findFirst();

        if (deporteExistente.isPresent()) {
            Deporte deporte = deporteExistente.get();
            if (Boolean.TRUE.equals(deporte.getVisibilidad())) {
                throw new DeporteYaExisteException("El deporte '" + nombreIngresado + "' ya existe en la base de datos.");
            } else {
                deporte.setVisibilidad(true);
                deporteRepository.save(deporte);
                return new DeporteResponse(deporte.getIdDeporte(), deporte.getNombreDeporte());
            }
        }

        Deporte nuevoDeporte = new Deporte(nombreIngresado, deporteRequest.getDescripcionDeporte());
        Deporte deporteGuardado = deporteRepository.save(nuevoDeporte);
        return new DeporteResponse(deporteGuardado.getIdDeporte(), deporteGuardado.getNombreDeporte());
    }



    @Transactional
    public List<DeporteResponse> obtenerTodosLosDeportesVisibles() {
        return deporteRepository.findAllByVisibilidadTrue()
                .stream()
                .map(DeporteResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeporteResponse actualizarDeporte(Integer idDeporte, DeporteRequest deporteRequest) {
        Deporte deporteExistente = deporteRepository.findById(idDeporte)
                .orElseThrow(() -> new DeporteNoExisteException("Deporte no encontrado con ID: " + idDeporte));

        String nombreNuevoNormalizado = TextoUtil.quitarAcentos(deporteRequest.getNombreDeporte().toLowerCase());
        Optional<Deporte> deporteConMismoNombre = deporteRepository.findAll().stream()
                .filter(d -> !d.getIdDeporte().equals(idDeporte)) // Excluir el actual
                .filter(d -> TextoUtil.quitarAcentos(d.getNombreDeporte().toLowerCase())
                        .equals(nombreNuevoNormalizado))
                .findFirst();
        System.out.println(deporteConMismoNombre);
        if (deporteConMismoNombre.isPresent()) {
            throw new DeporteYaExisteException("No se puede actualizar, ya existe un deporte con el nombre '" + deporteRequest.getNombreDeporte() + "'.");
        }

        deporteExistente.setNombreDeporte(deporteRequest.getNombreDeporte());
        deporteExistente.setDescripcionDeporte(deporteRequest.getDescripcionDeporte());

        Deporte deporteActualizado = deporteRepository.save(deporteExistente);
        return new DeporteResponse(deporteActualizado);
    }

    @Transactional
    public DeporteResponse softDeleteDeporte(Integer idDeporte) {
        Deporte deporteExistente = deporteRepository.findByIdDeporteAndVisibilidadTrue(idDeporte)
                .orElseThrow(() -> new DeporteNoExisteException("Deporte no encontrado con ID: " + idDeporte));

        deporteExistente.setVisibilidad(NO_VISIBLE);
        Deporte deporteEliminado = deporteRepository.save(deporteExistente);
        return new DeporteResponse(deporteEliminado);
    }
}
