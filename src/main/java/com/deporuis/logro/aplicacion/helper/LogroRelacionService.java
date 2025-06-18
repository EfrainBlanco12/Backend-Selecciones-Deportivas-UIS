package com.deporuis.logro.aplicacion.helper;

import com.deporuis.logro.dominio.Logro;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionLogro;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogroRelacionService {
    public List<SeleccionLogro> crearRelacionesSeleccion(Logro logro, List<Seleccion> selecciones) {
        return selecciones.stream()
                .map(seleccion -> {
                    SeleccionLogro sl = new SeleccionLogro();
                    sl.setLogro(logro);
                    sl.setSeleccion(seleccion);
                    return sl;
                })
                .collect(Collectors.toList());
    }
}
