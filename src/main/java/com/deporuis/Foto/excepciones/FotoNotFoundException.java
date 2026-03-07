package com.deporuis.Foto.excepciones;

public class FotoNotFoundException extends RuntimeException {
    public FotoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
