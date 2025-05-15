package com.deporuis.deporte.excepciones;

public class DeporteYaExisteException extends RuntimeException {
    public DeporteYaExisteException(String mensaje) {
        super(mensaje);
    }
}

