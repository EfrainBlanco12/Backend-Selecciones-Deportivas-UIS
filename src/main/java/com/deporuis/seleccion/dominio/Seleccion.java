package com.deporuis.seleccion.dominio;

import com.deporuis.integrante.dominio.Integrante;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Selecciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSeleccion;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "nombre_seleccion", length = 50)
    private String nombreSeleccion;

    @Column(name = "espacio_deportivo", length = 100)
    private String espacioDeportivo;

    private Boolean equipo;

    @Column(name = "id_deporte")
    private Integer idDeporte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('MASCULINO', 'FEMENINO', 'MIXTO') DEFAULT 'MIXTO'")
    private TipoSeleccion tipo;


    @OneToMany(mappedBy = "seleccion")
    private java.util.List<Integrante> integrantes;


}

