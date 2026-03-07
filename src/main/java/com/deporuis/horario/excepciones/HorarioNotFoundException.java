package com.deporuis.horario.excepciones;

public class HorarioNotFoundException extends RuntimeException{
    public HorarioNotFoundException( String message) {
        super(message);
    }
}
