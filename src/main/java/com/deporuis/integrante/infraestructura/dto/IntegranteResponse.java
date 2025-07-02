package com.deporuis.integrante.infraestructura.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class IntegranteResponse {
    private Integer idIntegrante;

    private String codigoUniversitario;

    private String nombres;

    private String apellidos;

    private LocalDate fechaNacimiento;

    private Float altura;

    private Float peso;

    private Integer dorsal;

    private String correoUniversitario;

    private Integer idRol;

    private Integer idSeleccion;

    private Integer idFoto;

    private List<Integer> idPosiciones;
}
