package com.deporuis.seleccion.dominio;

import com.deporuis.Foto.dominio.Foto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Selecciones_Fotos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeleccionFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seleccion_foto")
    private Integer idSeleccionFoto;

    @ManyToOne
    @JoinColumn(name = "id_seleccion", nullable = false)
    private Seleccion seleccion;

    @ManyToOne
    @JoinColumn(name = "id_foto", nullable = false)
    private Foto foto;
}
