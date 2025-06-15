package com.deporuis.publicacion.aplicacion.helper;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.PublicacionFoto;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionRelacionService {

    public List<SeleccionPublicacion> crearRelacionesSeleccion(Publicacion publicacion, List<Seleccion> selecciones) {
        return selecciones.stream()
                .map(seleccion -> {
                    SeleccionPublicacion sp = new SeleccionPublicacion();
                    sp.setPublicacion(publicacion);
                    sp.setSeleccion(seleccion);
                    return sp;
                })
                .collect(Collectors.toList());
    }

    public List<PublicacionFoto> crearRelacionesFoto(Publicacion publicacion, List<Foto> fotos) {
        return fotos.stream()
                .map(foto -> {
                    PublicacionFoto pf = new PublicacionFoto();
                    pf.setPublicacion(publicacion);
                    pf.setFoto(foto);
                    return pf;
                })
                .collect(Collectors.toList());
    }
}
