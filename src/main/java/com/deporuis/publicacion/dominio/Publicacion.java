package com.deporuis.publicacion.dominio;

import com.deporuis.Foto.dominio.Foto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(length = 500, nullable = false)
    private String descripcion;

    @Column(length = 50)
    private String lugar;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 20, nullable = false)
    private String duracion;

    private Boolean visibilidad = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_publicacion", nullable = false, columnDefinition = "ENUM('NOTICIA', 'EVENTO') default 'NOTICIA'")
    private TipoPublicacion tipoPublicacion = TipoPublicacion.NOTICIA;

    //Creacion de una publicacion

    public Publicacion(String titulo, String descripcion, String lugar, LocalDateTime fecha, String duracion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fecha = fecha;
        this.duracion = duracion;
    }
}
