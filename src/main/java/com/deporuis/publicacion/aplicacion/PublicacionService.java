package com.deporuis.publicacion.aplicacion;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    public PublicacionResponse crearPublicacion(PublicacionRequest publicacionRequest) {

        //TODO: Mirar si hay excepciones

        String titulo = publicacionRequest.getTitulo();
        String descripcion = publicacionRequest.getDescripcion();
        String lugar = publicacionRequest.getLugar();
//        lugar = (lugar != null) ? lugar : "";
        LocalDateTime fecha = publicacionRequest.getFecha();
        String duracion = publicacionRequest.getDuracion();
        Foto foto = publicacionRequest.getFoto();

        Publicacion nuevaPublicacion = new Publicacion(titulo, descripcion, lugar, fecha, duracion, foto);
        Publicacion publicacionGuardada = publicacionRepository.save(nuevaPublicacion);

        int idNuevaPublicacion = publicacionGuardada.getIdPublicacion();
        String tituloNuevaPublicacion = publicacionGuardada.getTitulo();

        return new PublicacionResponse(idNuevaPublicacion, tituloNuevaPublicacion);

    }
}
