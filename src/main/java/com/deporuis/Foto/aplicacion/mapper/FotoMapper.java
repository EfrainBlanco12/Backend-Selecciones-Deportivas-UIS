package com.deporuis.Foto.aplicacion.mapper;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;

import java.util.List;

public class FotoMapper {
    public static Foto requestToFoto(FotoRequest fotoRequest) {
        return new Foto(
                fotoRequest.getContenido(),
                fotoRequest.getTemporada()
        );
    }


    public static FotoResponse toResponse(Foto foto) {
        FotoResponse response = new FotoResponse();
        response.setIdFoto(foto.getIdFoto());
        response.setContenido(foto.getContenido());
        response.setTemporada(foto.getTemporada());
        
        if (foto.getIntegrante() != null) {
            response.setIdIntegrante(foto.getIntegrante().getIdIntegrante());
        }
        
        if (foto.getSeleccion() != null) {
            response.setIdSeleccion(foto.getSeleccion().getIdSeleccion());
        }
        
        if (foto.getPublicacion() != null) {
            response.setIdPublicacion(foto.getPublicacion().getIdPublicacion());
        }
        
        return response;
    }

    public static List<Foto> requestToFotosPublicacion(List<FotoRequest> fotoRequest, Publicacion publicacion){
        return fotoRequest.stream()
                .map(fotoReq -> {
                    Foto foto = new Foto();
                    foto.setContenido(fotoReq.getContenido());
                    foto.setTemporada(fotoReq.getTemporada());
                    foto.setPublicacion(publicacion);
                    return foto;
                })
                .toList();
    }

    public static List<Foto> requesToFotosSeleccion(List<FotoRequest> fotos, Seleccion seleccion) {
        return fotos.stream()
                .map(fotoRequest -> {
                    Foto foto = new Foto();
                    foto.setContenido(fotoRequest.getContenido());
                    foto.setTemporada(fotoRequest.getTemporada());
                    foto.setSeleccion(seleccion);
                    return foto;
                })
                .toList();
    }
}
