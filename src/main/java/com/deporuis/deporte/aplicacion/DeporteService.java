package com.deporuis.deporte.aplicacion;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DeporteService {

    @Autowired
    private DeporteCommandService deporteCommandService;

    @Transactional()
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
}
