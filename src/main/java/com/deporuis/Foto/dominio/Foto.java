package com.deporuis.Foto.dominio;

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

    @Column(name = "url_foto", length = 200, nullable = false)
    private String urlFoto;

    private Integer temporada;
}

