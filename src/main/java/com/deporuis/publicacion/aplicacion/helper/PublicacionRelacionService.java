package com.deporuis.publicacion.aplicacion.helper;

import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import com.deporuis.seleccion.infraestructura.SeleccionPublicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    public List<SeleccionPublicacion> obtenerRelacionesSeleccion(Publicacion publicacion){
        return seleccionPublicacionRepository.findAllByPublicacion(publicacion);
    }

    public List<SeleccionPublicacion> actualizarRelacionesSeleccion(Publicacion publicacion, List<Seleccion> nuevasSelecciones, List<Integer> idSelecciones) {
        List<SeleccionPublicacion> actualesSeleccion = obtenerRelacionesSeleccion(publicacion);

        Set<Integer> actualesIdsSeleccion = actualesSeleccion.stream()
                .map(sp -> sp.getSeleccion().getIdSeleccion())
                .collect(Collectors.toSet());
        Set<Integer> nuevasIdsSeleccion = new HashSet<>(idSelecciones);

        List<SeleccionPublicacion> toRemove = actualesSeleccion.stream()
                .filter(sp -> !nuevasIdsSeleccion.contains(sp.getSeleccion().getIdSeleccion()))
                .collect(Collectors.toList());
        seleccionPublicacionRepository.deleteAll(toRemove);

        List<Seleccion> toAdd = nuevasSelecciones.stream()
                .filter(s -> !actualesIdsSeleccion.contains(s.getIdSeleccion()))
                .collect(Collectors.toList());
        List<SeleccionPublicacion> nuevasRelaciones = crearRelacionesSeleccion(publicacion, toAdd);
        seleccionPublicacionRepository.saveAll(nuevasRelaciones);

        return seleccionPublicacionRepository.findAllByPublicacion(publicacion);
    }
}
