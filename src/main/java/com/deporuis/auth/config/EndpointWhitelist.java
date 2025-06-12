package com.deporuis.auth.config;

public class EndpointWhitelist {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/public/**"
    };

    public static final String[] PRIVATE_ENDPOINTS = {
            "/private/**"
    };

//    public static final String[] ENTRENADOR_ENDPOINTS = {
//            "/entrenador/**"
//    };
}

