package com.deporuis.posicion.infraestructura.dto;

import com.deporuis.posicion.dominio.Posicion;
import lombok.Data;

@Data
public class PosicionResponse {

    private Integer idPosicion;
    private String nombrePosicion;
    private String nombreDeporte;

    public PosicionResponse(Posicion posicion) {
        this.idPosicion = posicion.getIdPosicion();
        this.nombrePosicion = posicion.getNombrePosicion();
        this.nombreDeporte = posicion.getDeporte().getNombreDeporte();
    }
}
