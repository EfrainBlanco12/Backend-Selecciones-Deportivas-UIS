package com.deporuis.deporte.infraestructura.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeporteResponse {

    private Integer idDeporte;

    private String nombreDeporte;

    public DeporteResponse(Integer idDeporte, String nombreDeporte) {
        this.idDeporte = idDeporte;
        this.nombreDeporte = nombreDeporte;
    }
}
