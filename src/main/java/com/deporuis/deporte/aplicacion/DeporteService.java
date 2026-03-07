package com.deporuis.deporte.aplicacion;

import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeporteService {

    @Autowired
    private DeporteCommandService deporteCommandService;

    @Autowired
    private DeporteQueryService deporteQueryService;

    @Transactional
    public DeporteResponse crearDeporte(DeporteRequest deporteRequest) {
        return deporteCommandService.crearDeporte(deporteRequest);
    }

    @Transactional(readOnly = true)
    public List<DeporteResponse> obtenerTodosLosDeportesVisibles() {
        return deporteQueryService.obtenerTodosLosDeportesVisibles();
    }

    @Transactional
    public DeporteResponse actualizarDeporte(Integer idDeporte, DeporteRequest deporteRequest) {
        return deporteCommandService.actualizarDeporte(idDeporte, deporteRequest);
    }

    @Transactional
    public DeporteResponse softDeleteDeporte(Integer idDeporte) {
        return deporteCommandService.softDeleteDeporte(idDeporte);
    }


}
