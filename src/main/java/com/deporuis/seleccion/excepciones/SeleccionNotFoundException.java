package com.deporuis.seleccion.excepciones;

import com.deporuis.excepcion.common.ResourceNotFoundException;

public class SeleccionNotFoundException extends ResourceNotFoundException {
    public SeleccionNotFoundException(String msg) {
        super(msg);
    }
}
