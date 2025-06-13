package com.deporuis.Foto.dominio;

import com.deporuis.integrante.dominio.Integrante;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

//    @Column(name = "url_foto", length = 500, nullable = false)
//    private String urlFoto;

    @Lob
    @Column(name = "contenido", columnDefinition = "MEDIUMBLOB")
    private byte[] contenido;

    @Column(name = "temporada")
    private Integer temporada;

    // Relación inversa con Integrantes
    @OneToMany(mappedBy = "foto")
    private List<Integrante> integrantes;
}
