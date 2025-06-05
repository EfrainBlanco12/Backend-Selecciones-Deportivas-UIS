package com.deporuis.auth.infraestructura.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String codigo_universitario;
    private String password;
}

