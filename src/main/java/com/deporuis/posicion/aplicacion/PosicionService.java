package com.deporuis.posicion.aplicacion;

import com.deporuis.posicion.infraestructura.dto.PosicionActualizarRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PosicionService {

    @Autowired
    private PosicionCommandService command;
    @Autowired
    private PosicionQueryService query;

    public PosicionResponse crearPosicion(PosicionRequest posicionRequest) {
        return command.crearPosicion(posicionRequest);
    }

    public List<PosicionResponse> obtenerPosicionPorDeporte(Integer idDeporte) {
        return query.obtenerPosicionPorDeporte(idDeporte);
    }


    public PosicionResponse actualizarPosicion(Integer id, PosicionActualizarRequest request) {
        return command.actualizarPosicion(id, request);
    }

    public PosicionResponse softDelete(Integer id) {
        return command.softDelete(id);
    }
}
