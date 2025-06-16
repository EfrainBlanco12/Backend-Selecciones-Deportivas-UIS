package com.deporuis.deporte.infraestructura.dto;

import com.deporuis.deporte.dominio.Deporte;
import lombok.Data;

@Data
public class DeporteResponse {

    private Integer idDeporte;
    private String nombreDeporte;
    private String descripcionDeporte;

    public DeporteResponse(Integer idDeporte, String nombreDeporte) {
        this.idDeporte = idDeporte;
        this.nombreDeporte = nombreDeporte;
    }

    public DeporteResponse(Deporte deporte) {
        this.idDeporte = deporte.getIdDeporte();
        this.nombreDeporte = deporte.getNombreDeporte();
        this.descripcionDeporte = deporte.getDescripcionDeporte();
    }
}
