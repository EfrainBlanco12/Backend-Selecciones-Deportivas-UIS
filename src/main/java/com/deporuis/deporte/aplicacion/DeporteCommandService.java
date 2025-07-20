package com.deporuis.deporte.aplicacion;

import com.deporuis.deporte.aplicacion.helper.DeporteVerificarExistenciaService;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteNotFoundException;
import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import com.deporuis.shared.util.TextoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class DeporteCommandService {

    @Autowired
    private DeporteRepository deporteRepository;

    @Autowired
    private DeporteQueryService deporteQueryService;

    @Autowired
    private DeporteVerificarExistenciaService deporteVerificarExistenciaService;

    @Transactional
    public DeporteResponse crearDeporte(DeporteRequest deporteRequest) {
        Optional<Deporte> deporteReactivado = deporteVerificarExistenciaService.verificarDeporteNoExisteYReactivarSiAplica(deporteRequest);

        if (deporteReactivado.isPresent()) {
            Deporte deporte = deporteReactivado.get();
            return new DeporteResponse(deporte.getIdDeporte(), deporte.getNombreDeporte());
        }

        Deporte nuevoDeporte = new Deporte(deporteRequest.getNombreDeporte(), deporteRequest.getDescripcionDeporte());
        Deporte deporteGuardado = deporteRepository.save(nuevoDeporte);
        return new DeporteResponse(deporteGuardado.getIdDeporte(), deporteGuardado.getNombreDeporte());
    }



    @Transactional
    public DeporteResponse actualizarDeporte(Integer idDeporte, DeporteRequest deporteRequest) {
        Deporte deporteExistente = deporteQueryService.buscarPorId(idDeporte)
                .orElseThrow(() -> new DeporteNotFoundException("Deporte no encontrado con ID: " + idDeporte));

        deporteVerificarExistenciaService.validarNombreNoDuplicado(deporteRequest.getNombreDeporte(), idDeporte);

        deporteExistente.setNombreDeporte(deporteRequest.getNombreDeporte());
        deporteExistente.setDescripcionDeporte(deporteRequest.getDescripcionDeporte());

        Deporte deporteActualizado = deporteRepository.save(deporteExistente);
        return new DeporteResponse(deporteActualizado);
    }

    @Transactional
    public DeporteResponse softDeleteDeporte(Integer idDeporte) {
        Deporte deporteExistente = deporteQueryService.buscarPorId(idDeporte)
                .filter(Deporte::getVisibilidad)
                .orElseThrow(() -> new DeporteNotFoundException("Deporte no encontrado con ID: " + idDeporte));

        deporteExistente.setVisibilidad(false);

        Deporte deporteEliminado = deporteRepository.save(deporteExistente);
        return new DeporteResponse(deporteEliminado);
    }
}
