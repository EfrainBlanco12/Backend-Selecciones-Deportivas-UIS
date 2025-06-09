package com.deporuis.publicacion.dominio;

import com.deporuis.Foto.dominio.Foto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Publicaciones_Fotos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionFoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacion_foto")
    private Integer idPublicacionFoto;

    @ManyToOne
    @JoinColumn(name = "id_publicacion", nullable = false)
    private Publicacion publicacion;

    @ManyToOne
    @JoinColumn(name = "id_foto", nullable = false)
    private Foto foto;
}
