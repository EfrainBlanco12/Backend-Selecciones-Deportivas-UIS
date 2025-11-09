package com.deporuis.auth.excepciones;

public class LoginYaExisteException extends RuntimeException {
    public LoginYaExisteException(String message) {
        super(message);
    }
}
