package com.deporuis.publicacion.aplicacion.helper;

import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import com.deporuis.seleccion.infraestructura.SeleccionPublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicacionRelacionService {

    @Autowired
    private SeleccionPublicacionRepository seleccionPublicacionRepository;

    public List<SeleccionPublicacion> crearRelacionesSeleccion(Publicacion publicacion, List<Seleccion> selecciones) {
        return seleccionPublicacionRepository.saveAll(
        selecciones.stream()
                .map(seleccion -> {
                    SeleccionPublicacion sp = new SeleccionPublicacion();
                    sp.setPublicacion(publicacion);
                    sp.setSeleccion(seleccion);
                    return sp;
                })
                .collect(Collectors.toList())
        );
    }

    public void eliminarRelacionesSeleccion(Publicacion publicacion) {
        seleccionPublicacionRepository.deleteAll(seleccionPublicacionRepository.findAllByPublicacion(publicacion));
    }
}
