package com.deporuis.integrante.dominio;

import com.deporuis.posicion.dominio.Posicion;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Integrantes_Posiciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrantePosicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_integrante_posicion")
    private Integer idIntegrantePosicion;

    @ManyToOne
    @JoinColumn(name = "id_integrante", nullable = false)
    private Integrante integrante;

    @ManyToOne
    @JoinColumn(name = "id_posicion", nullable = false)
    private Posicion posicion;
}
