package com.deporuis.auth.config;

public class EndpointWhitelist {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**"
    };

    public static final String[] ADMIN_ENDPOINTS = {
            "/admin/**"
    };

    public static final String[] ENTRENADOR_ENDPOINTS = {
            "/entrenador/**"
    };
}

