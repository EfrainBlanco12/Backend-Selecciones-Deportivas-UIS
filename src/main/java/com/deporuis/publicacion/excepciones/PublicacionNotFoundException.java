package com.deporuis.publicacion.excepciones;

public class PublicacionNotFoundException extends RuntimeException {
    public PublicacionNotFoundException(String mensaje) {
        super(mensaje);
    }
}
