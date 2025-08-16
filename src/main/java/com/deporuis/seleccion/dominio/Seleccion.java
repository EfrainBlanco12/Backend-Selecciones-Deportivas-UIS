package com.deporuis.seleccion.dominio;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.integrante.dominio.Integrante;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Selecciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seleccion")
    private Integer idSeleccion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "nombre_seleccion", length = 50, nullable = false)
    private String nombreSeleccion;

    @Column(name = "espacio_deportivo", length = 100, nullable = false)
    private String espacioDeportivo;

    private Boolean equipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('MASCULINO', 'FEMENINO', 'MIXTO') default 'MIXTO'")
    private TipoSeleccion tipo_seleccion;

    private Boolean visibilidad = true;

    @ManyToOne
    @JoinColumn(name = "id_deporte", nullable = false)
    private Deporte deporte;

    @OneToMany(mappedBy = "seleccion")
    private List<Integrante> integrantes;

    @OneToMany(mappedBy = "seleccion")
    private List<SeleccionLogro> logros;

    // en Seleccion.java
    @BatchSize(size = 25)
    @OneToMany(mappedBy = "seleccion")
    private List<Foto> fotos = new ArrayList<>();

    @BatchSize(size = 25)
    @OneToMany(mappedBy = "seleccion")
    private List<SeleccionHorario> horarios;

    @OneToMany(mappedBy = "seleccion")
    private List<SeleccionPublicacion> publicaciones = new ArrayList<>();

    // Creacion de una seleccion
    public Seleccion(LocalDate fechaCreacion, String nombreSeleccion, String espacioDeportivo, Boolean equipo, TipoSeleccion tipo_seleccion) {
        this.fechaCreacion = fechaCreacion;
        this.nombreSeleccion = nombreSeleccion;
        this.espacioDeportivo = espacioDeportivo;
        this.equipo = equipo;
        this.tipo_seleccion = tipo_seleccion;
    }
}
