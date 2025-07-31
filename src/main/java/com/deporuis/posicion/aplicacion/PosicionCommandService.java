package com.deporuis.posicion.aplicacion;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteNotFoundException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.posicion.aplicacion.helper.PosicionVerificarExistenciaService;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.excepciones.PosicionNotFoundException;
import com.deporuis.posicion.infraestructura.PosicionRepository;
import com.deporuis.posicion.infraestructura.dto.PosicionActualizarRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PosicionCommandService {

    @Autowired
    private PosicionRepository posicionRepository;

    @Autowired
    private DeporteRepository deporteRepository;

    @Autowired
    private PosicionVerificarExistenciaService posicionVerificarExistenciaService;

    @Transactional
    public PosicionResponse crearPosicion(PosicionRequest posicionRequest) {
        Deporte deporte = deporteRepository.findById(posicionRequest.getIdDeporte())
                .orElseThrow(() -> new DeporteNotFoundException("Deporte no encontrado"));

        Optional<Posicion> reactivada = posicionVerificarExistenciaService
                .verificarPosicionNoExisteYReactivarSiAplica(posicionRequest);

        if (reactivada.isPresent()) {
            return new PosicionResponse(reactivada.get());
        }

        Posicion nueva = new Posicion();
        nueva.setNombrePosicion(posicionRequest.getNombrePosicion());
        nueva.setDeporte(deporte);
        nueva.setVisibilidad(true);

        return new PosicionResponse(posicionRepository.save(nueva));
    }


    @Transactional
    public PosicionResponse actualizarPosicion(Integer id, PosicionActualizarRequest request) {
        Posicion existente = posicionRepository.findByIdPosicionAndVisibilidadTrue(id)
                .orElseThrow(() -> new PosicionNotFoundException("Posición no encontrada"));

        existente.setNombrePosicion(request.getNombrePosicion());
        return new PosicionResponse(posicionRepository.save(existente));
    }

    @Transactional
    public PosicionResponse softDelete(Integer id) {
        Posicion posicion = posicionRepository.findById(id)
                .orElseThrow(() -> new PosicionNotFoundException("Posición no encontrada"));
        posicion.setVisibilidad(false);
        return new PosicionResponse(posicionRepository.save(posicion));
    }
}
