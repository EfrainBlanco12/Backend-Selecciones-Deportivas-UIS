package com.deporuis.deporte.excepciones;

import com.deporuis.excepcion.common.ResourceNotFoundException;

public class DeporteYaExisteException extends ResourceNotFoundException {
    public DeporteYaExisteException(String mensaje) {
        super(mensaje);
    }
}

