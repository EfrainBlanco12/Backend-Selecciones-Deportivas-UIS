package com.deporuis.integrante.excepciones;

import com.deporuis.excepcion.common.BadRequestException;

public class IntegranteDobleUniqueKeyException extends BadRequestException {
    public IntegranteDobleUniqueKeyException(String msg) {
        super(msg);
    }
}
