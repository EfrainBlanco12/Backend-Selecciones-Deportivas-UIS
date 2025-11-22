package com.deporuis.integrante.infraestructura.dto;

import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private Integer idSeleccion;

    private RolResponse rol;
    private List<FotoResponse> fotos;
    private List<PosicionResponse> posiciones;
}
