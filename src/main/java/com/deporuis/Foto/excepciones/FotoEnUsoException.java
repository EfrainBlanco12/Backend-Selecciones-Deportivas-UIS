package com.deporuis.Foto.excepciones;

public class FotoEnUsoException extends RuntimeException {
    public FotoEnUsoException(String mensaje) {
        super(mensaje);
    }
}
