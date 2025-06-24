package com.deporuis.logro.aplicacion.helper;

import com.deporuis.logro.dominio.Logro;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionLogro;
import com.deporuis.seleccion.infraestructura.SeleccionLogroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LogroRelacionService {

    @Autowired
    private SeleccionLogroRepository seleccionLogroRepository;

    public List<SeleccionLogro> crearRelacionesSeleccion(Logro logro, List<Seleccion> selecciones) {
        List<SeleccionLogro> seleccionesLogros = selecciones.stream()
                .map(seleccion -> {
                    SeleccionLogro sl = new SeleccionLogro();
                    sl.setLogro(logro);
                    sl.setSeleccion(seleccion);
                    return sl;
                })
                .toList();
        return seleccionLogroRepository.saveAll(seleccionesLogros);
    }

    public List<SeleccionLogro> actualizarRelacionesSeleccion(Logro logro, List<Seleccion> nuevasSelecciones, List<Integer> idSelecciones) {
        List<SeleccionLogro> actualesSeleccion = seleccionLogroRepository.findAllByLogro(logro);

        Set<Integer> actualesIdsSeleccion = actualesSeleccion.stream()
                .map(sp -> sp.getSeleccion().getIdSeleccion())
                .collect(Collectors.toSet());
        Set<Integer> nuevasIdsSeleccion = new HashSet<>(idSelecciones);

        List<SeleccionLogro> toRemoveSeleccion = actualesSeleccion.stream()
                .filter(sp -> !nuevasIdsSeleccion.contains(sp.getSeleccion().getIdSeleccion()))
                .collect(Collectors.toList());
        seleccionLogroRepository.deleteAll(toRemoveSeleccion);

        List<Seleccion> seleccionesToAdd = nuevasSelecciones.stream()
                .filter(s -> !actualesIdsSeleccion.contains(s.getIdSeleccion()))
                .collect(Collectors.toList());
        seleccionLogroRepository.saveAll(crearRelacionesSeleccion(logro, seleccionesToAdd));

        return seleccionLogroRepository.findAllByLogro(logro);
    }

    public void eliminarRelacionesSeleccion(Logro logro) {
        seleccionLogroRepository.deleteAll(seleccionLogroRepository.findAllByLogro(logro));
    }
}
