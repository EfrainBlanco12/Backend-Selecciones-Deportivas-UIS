package com.deporuis.seleccion.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeleccionPublicacionResponse {
        private Integer idSeleccion;
        private String nombreSeleccion;
}
