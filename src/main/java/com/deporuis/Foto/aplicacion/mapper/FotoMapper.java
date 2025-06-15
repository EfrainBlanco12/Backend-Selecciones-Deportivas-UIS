package com.deporuis.Foto.aplicacion.mapper;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;

public class FotoMapper {
    public static Foto requestToFoto(FotoRequest fotoRequest) {
        return new Foto(
                fotoRequest.getContenido(),
                fotoRequest.getTemporada()
        );
    }

    public static FotoResponse toResponse(Foto foto) {
        return new FotoResponse(
                foto.getIdFoto(),
                foto.getContenido(),
                foto.getTemporada()
        );
    }
}
