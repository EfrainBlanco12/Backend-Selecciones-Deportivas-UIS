package com.deporuis.Foto.dominio;

import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Fotos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Integer idFoto;

    @Lob
    @Column(name = "contenido", columnDefinition = "MEDIUMBLOB")
    private byte[] contenido;

    @Column(name = "temporada")
    private Integer temporada;

    // Relación con Integrante (Foto tiene id_integrante como FK)
    @ManyToOne
    @JoinColumn(name = "id_integrante")
    private Integrante integrante;

    // Relación con Selección (Foto tiene id_seleccion como FK)
    @ManyToOne
    @JoinColumn(name = "id_seleccion")
    private Seleccion seleccion;

    // Relación con Publicación (Foto tiene id_publicacion como FK)
    @ManyToOne
    @JoinColumn(name = "id_publicacion")
    private Publicacion publicacion;

    public Foto(byte[] contenido, Integer temporada) {
        this.contenido = contenido;
        this.temporada = temporada;
    }
}
