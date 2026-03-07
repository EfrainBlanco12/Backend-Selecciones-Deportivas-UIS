package com.deporuis.publicacion.dominio;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(length = 20)
    private String duracion;

    private Boolean visibilidad = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_publicacion", nullable = false, columnDefinition = "ENUM('NOTICIA', 'EVENTO') default 'NOTICIA'")
    private TipoPublicacion tipoPublicacion = TipoPublicacion.NOTICIA;

    @Column(name = "usuario_modifico")
    private Integer usuarioModifico;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "publicacion")
    private List<Foto> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "publicacion")
    private List<SeleccionPublicacion> selecciones = new ArrayList<>();

    public Publicacion(String titulo, String descripcion, String lugar, LocalDateTime fecha, String duracion, TipoPublicacion tipoPublicacion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fecha = fecha;
        this.duracion = duracion;
        this.tipoPublicacion = tipoPublicacion;
        this.fechaModificacion = LocalDateTime.now();
    }
}
