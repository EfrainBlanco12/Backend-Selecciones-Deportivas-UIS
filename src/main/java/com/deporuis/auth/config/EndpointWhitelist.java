package com.deporuis.auth.config;

public class EndpointWhitelist {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/public/**"
    };

    public static final String[] ADMIN_ENDPOINTS = {
            "/admin/**",
            "/deporte/**"
    };

    public static final String[] ENTRENADOR_ENDPOINTS = {
            "/entrenador/**"
    };
}

