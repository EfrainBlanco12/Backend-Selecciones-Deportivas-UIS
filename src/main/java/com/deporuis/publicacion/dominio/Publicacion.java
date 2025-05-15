package com.deporuis.publicacion.dominio;

import com.deporuis.Foto.dominio.Foto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Publicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacion")
    private Integer idPublicacion;

    @Column(length = 50, nullable = false)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Column(length = 50)
    private String lugar;

    private LocalDateTime fecha;

    @Column(length = 20)
    private String duracion;

    @ManyToOne
    @JoinColumn(name = "id_foto")
    private Foto foto;
}

