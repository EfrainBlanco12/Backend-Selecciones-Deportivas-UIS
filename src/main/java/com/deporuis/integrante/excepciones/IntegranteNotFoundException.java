package com.deporuis.integrante.excepciones;

import com.deporuis.excepcion.common.ResourceNotFoundException;

public class IntegranteNotFoundException extends ResourceNotFoundException {
    public IntegranteNotFoundException(String msg) {
        super(msg);
    }
}
