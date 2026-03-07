package com.deporuis.seleccion.dominio;

import com.deporuis.logro.dominio.Logro;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Selecciones_Logros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeleccionLogro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seleccion_logro")
    private Integer idSeleccionLogro;

    @ManyToOne
    @JoinColumn(name = "id_seleccion", nullable = false)
    private Seleccion seleccion;

    @ManyToOne
    @JoinColumn(name = "id_logro", nullable = false)
    private Logro logro;
}
