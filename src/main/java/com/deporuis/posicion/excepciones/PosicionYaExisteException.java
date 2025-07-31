package com.deporuis.posicion.excepciones;

public class PosicionYaExisteException extends RuntimeException {
    public PosicionYaExisteException(String message) {
        super(message);
    }
}
