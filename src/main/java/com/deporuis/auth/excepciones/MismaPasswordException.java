package com.deporuis.auth.excepciones;

public class MismaPasswordException extends RuntimeException {
    public MismaPasswordException(String mensaje) {
        super(mensaje);
    }
}
