package com.deporuis.posicion.dominio;

import com.deporuis.deporte.dominio.Deporte;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Posiciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_posicion")
    private Integer idPosicion;

    @Column(name = "nombre_posicion", length = 50, nullable = false)
    private String nombrePosicion;

    @ManyToOne
    @JoinColumn(name = "id_deporte", nullable = false)
    private Deporte deporte;
}
