package com.deporuis.Foto.infraestructura.dto;

import java.util.List;

public class FotoResponse {
    private List<Integer> idsFotos;

    public FotoResponse(List<Integer> idsFotos) {
        this.idsFotos = idsFotos;
    }
}
