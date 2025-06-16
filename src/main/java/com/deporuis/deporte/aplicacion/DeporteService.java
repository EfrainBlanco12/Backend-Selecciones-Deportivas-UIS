package com.deporuis.deporte.aplicacion;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import com.deporuis.shared.util.TextoUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeporteService {

    @Autowired
    private DeporteRepository deporteRepository;

    @Transactional
    public DeporteResponse crearDeporte(DeporteRequest deporteRequest) {
        String nombre = deporteRequest.getNombreDeporte().toLowerCase();

        Optional<Deporte> deporteExistente = deporteRepository.findByNombreDeporte(nombre);
        if (deporteExistente.isPresent()) {
            throw new DeporteYaExisteException("El deporte '" + nombre + "' ya existe en la base de datos.");
        }

        Deporte nuevoDeporte = new Deporte(nombre, deporteRequest.getDescripcionDeporte());
        Deporte deporteGuardado = deporteRepository.save(nuevoDeporte);
        return new DeporteResponse(deporteGuardado.getIdDeporte(), deporteGuardado.getNombreDeporte());
    }

    public List<DeporteResponse> obtenerTodosLosDeportes() {
        return deporteRepository.findAll()
                .stream()
                .map(DeporteResponse::new)
                .collect(Collectors.toList());
    }

    public DeporteResponse actualizarDeporte(Integer idDeporte, DeporteRequest deporteRequest) {
        Deporte deporteExistente = deporteRepository.findById(idDeporte)
                .orElseThrow(() -> new EntityNotFoundException("Deporte no encontrado con ID: " + idDeporte));

        String nombreNuevoNormalizado = TextoUtil.quitarAcentos(deporteRequest.getNombreDeporte().toLowerCase());
        Optional<Deporte> deporteConMismoNombre = deporteRepository.findAll().stream()
                .filter(d -> !d.getIdDeporte().equals(idDeporte)) // Excluir el actual
                .filter(d -> TextoUtil.quitarAcentos(d.getNombreDeporte().toLowerCase())
                        .equals(nombreNuevoNormalizado))
                .findFirst();

        if (deporteConMismoNombre.isPresent()) {
            throw new DeporteYaExisteException("No se puede actualizar, ya existe un deporte con el nombre '" + deporteRequest.getNombreDeporte() + "'.");
        }

        deporteExistente.setNombreDeporte(deporteRequest.getNombreDeporte());
        deporteExistente.setDescripcionDeporte(deporteRequest.getDescripcionDeporte());

        Deporte deporteActualizado = deporteRepository.save(deporteExistente);
        return new DeporteResponse(deporteActualizado);
    }




}
