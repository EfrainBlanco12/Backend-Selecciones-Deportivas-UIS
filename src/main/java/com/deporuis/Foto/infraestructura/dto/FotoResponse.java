package com.deporuis.Foto.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FotoResponse {

    private Integer idFoto;

    private byte[] contenido;

    private Integer temporada;
}
