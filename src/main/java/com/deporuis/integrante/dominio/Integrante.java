package com.deporuis.integrante.dominio;

import com.deporuis.auth.dominio.Login;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.seleccion.dominio.Seleccion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Integrantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Integrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_integrante")
    private Integer idIntegrante;

    @Column(name = "codigo_universitario", length = 50, nullable = false, unique = true)
    private String codigoUniversitario;

    @Column(length = 50, nullable = false)
    private String nombres;

    @Column(length = 50, nullable = false)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column
    private Float altura;

    @Column
    private Float peso;

    @Column
    private Integer dorsal;

    @Column(name = "correo_institucional", length = 100, nullable = false, unique = true)
    private String correoInstitucional;

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_seleccion", nullable = false)
    private Seleccion seleccion;

    @OneToOne
    @JoinColumn(name = "id_foto", nullable = false)
    private Foto foto;

    @OneToMany(mappedBy = "integrante")
    private List<IntegrantePosicion> posiciones = new ArrayList<>();

    // Creacion de un integrante
    public Integrante(String codigoUniversitario,
                      String nombres,
                      String apellidos,
                      LocalDate fechaNacimiento,
                      Float altura,
                      Float peso,
                      Integer dorsal,
                      String correoInstitucional
    ) {
        this.codigoUniversitario = codigoUniversitario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.altura = altura;
        this.peso = peso;
        this.dorsal = dorsal;
        this.correoInstitucional = correoInstitucional;
    }
}
