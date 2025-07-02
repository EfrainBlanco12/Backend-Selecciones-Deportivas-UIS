package com.deporuis.integrante.infraestructura.dto;

import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class IntegranteRequest {

    @NotBlank(message = "El codigo universitario es obligatorio")
    private String codigoUniversitario;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellidos;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    private Float altura;

    private Float peso;

    private Integer dorsal;

    @NotBlank(message = "El correo institucional es obligatorio")
    private String correoInstitucional;

    @NotNull(message = "El rol es obligatorio")
    private Integer idRol = 3;

    @NotNull(message = "La seleccion del integrante es obligatoria")
    private Integer idSeleccion;

    @NotNull(message = "La foto es obligatoria")
    private FotoRequest foto;

    private List<Integer> idPosiciones = new ArrayList<>();
}
