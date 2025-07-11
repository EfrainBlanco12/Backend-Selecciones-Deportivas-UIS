package com.deporuis.deporte.excepciones;

public class DeporteNoExisteException extends RuntimeException {
    public DeporteNoExisteException(String message) {
        super(message);
    }
}
