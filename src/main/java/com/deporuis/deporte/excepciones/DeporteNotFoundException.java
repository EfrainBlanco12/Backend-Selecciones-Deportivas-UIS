package com.deporuis.deporte.excepciones;

import com.deporuis.excepcion.common.ResourceNotFoundException;

public class DeporteNotFoundException extends ResourceNotFoundException {
    public DeporteNotFoundException(String msg) {
        super(msg);
    }
}
