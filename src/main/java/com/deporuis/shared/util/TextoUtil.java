package com.deporuis.shared.util;

import java.text.Normalizer;

public class TextoUtil {

    // Elimina acentos y otros caracteres especiales de un texto, dejando solo caracteres ASCII planos.
    public static String quitarAcentos(String texto) {
        if (texto == null) return null;
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{M}", "");
    }
}
