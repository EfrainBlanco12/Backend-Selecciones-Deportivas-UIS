package com.deporuis.Foto.dominio;

import com.deporuis.integrante.dominio.Integrante;
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

    // Relación inversa con Integrantes
    @OneToOne(mappedBy = "foto")
    private Integrante integrante;

    public Foto(byte[] contenido, Integer temporada) {
        this.contenido = contenido;
        this.temporada = temporada;
    }
}
