package com.deporuis.posicion.excepciones;

import com.deporuis.excepcion.common.ResourceNotFoundException;

public class PosicionNotFoundException extends ResourceNotFoundException {
    public PosicionNotFoundException(String msg) {
        super(msg);
    }
}
