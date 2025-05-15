package com.deporuis.integrante.dominio;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.auth.dominio.Login;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.seleccion.dominio.Seleccion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "codigo_universitario", unique = true, length = 50, nullable = false)
    private String codigoUniversitario;

    @Column(length = 50, nullable = false)
    private String nombres;

    @Column(length = 50, nullable = false)
    private String apellidos;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private Float altura;
    private Float peso;

    @Column(length = 100)
    private String dorsal;

    @Column(name = "correo_institucional", unique = true, length = 100, nullable = false)
    private String correoInstitucional;

    // Relaciones con otras tablas
    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_seleccion")
    private Seleccion seleccion;

    @ManyToOne
    @JoinColumn(name = "id_foto")
    private Foto foto;

    @OneToOne(mappedBy = "integrante")
    private Login login;
}

