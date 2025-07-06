package com.deporuis.auth.excepciones;

import com.deporuis.excepcion.common.BadRequestException;

public class AccesoDenegadoCreacionIntegrantesException extends BadRequestException {
    public AccesoDenegadoCreacionIntegrantesException(String msg) {
        super(msg);
    }
}
