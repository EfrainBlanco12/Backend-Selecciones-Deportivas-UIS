package com.deporuis.Foto.infraestructura.dto;

import lombok.Data;

@Data
public class FotoResponse {

    private Integer idFoto;

    private byte[] contenido;

    private Integer temporada;

    public FotoResponse(Integer idFoto, byte[] contenido, Integer temporada) {
        this.idFoto = idFoto;
        this.contenido = contenido;
        this.temporada = temporada;
    }
}
