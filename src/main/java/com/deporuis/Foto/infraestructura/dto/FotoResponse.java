package com.deporuis.Foto.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FotoResponse {

    private Integer idFoto;

    private byte[] contenido;

    private Integer temporada;

}
