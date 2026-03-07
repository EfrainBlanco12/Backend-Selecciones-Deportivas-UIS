package com.deporuis.deporte.aplicacion.helper;

import com.deporuis.deporte.aplicacion.DeporteQueryService;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteNotFoundException;
import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.shared.util.TextoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DeporteVerificarExistenciaService {

    @Autowired
    private DeporteRepository deporteRepository;

    @Autowired
    private DeporteQueryService deporteQueryService;

    @Transactional(readOnly = true)
    public Deporte verificarDeporte(Integer id) {
        Optional<Deporte> deporte = deporteRepository.findById(id);

        if (deporte.isEmpty()) {
            throw new DeporteNotFoundException("No se encontro Deporte con ID = " + id);
        }

        return deporte.get();
    }

    @Transactional
    public Optional<Deporte> verificarDeporteNoExisteYReactivarSiAplica(DeporteRequest deporteRequest) {
        Optional<Deporte> deporteExistente = deporteQueryService.buscarPorNombreNormalizado(deporteRequest.getNombreDeporte());

        if (deporteExistente.isPresent()) {
            Deporte deporte = deporteExistente.get();

            if (Boolean.TRUE.equals(deporte.getVisibilidad())) {
                throw new DeporteYaExisteException("El deporte '" + deporteRequest.getNombreDeporte() + "' ya existe en la base de datos.");
            } else {
                deporte.setVisibilidad(true);
                deporte.setDescripcionDeporte(deporteRequest.getDescripcionDeporte());
                return Optional.of(deporteRepository.save(deporte));
            }
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public void validarNombreNoDuplicado(String nombreDeporte, Integer idAExcluir) {
        String nombreNormalizado = TextoUtil.quitarAcentos(nombreDeporte).toLowerCase();
        boolean existeDuplicado = deporteQueryService.existeNombreNormalizadoExcluyendoId(nombreNormalizado, idAExcluir);
        if (existeDuplicado) {
            throw new DeporteYaExisteException("Ya existe un deporte con el nombre '" + nombreDeporte + "'.");
        }
    }
}
