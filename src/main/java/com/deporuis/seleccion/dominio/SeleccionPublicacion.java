package com.deporuis.seleccion.dominio;

import com.deporuis.publicacion.dominio.Publicacion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Selecciones_Publicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeleccionPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seleccion_publicacion")
    private Integer idSeleccionPublicacion;

    @ManyToOne
    @JoinColumn(name = "id_seleccion", nullable = false)
    private Seleccion seleccion;

    @ManyToOne
    @JoinColumn(name = "id_publicacion", nullable = false)
    private Publicacion publicacion;
}
