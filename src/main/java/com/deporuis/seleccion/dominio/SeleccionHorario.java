package com.deporuis.seleccion.dominio;

import com.deporuis.horario.dominio.Horario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Selecciones_Horarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeleccionHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seleccion_horario")
    private Integer idSeleccionHorario;

    @ManyToOne
    @JoinColumn(name = "id_seleccion", nullable = false)
    private Seleccion seleccion;

    @ManyToOne
    @JoinColumn(name = "id_horario", nullable = false)
    private Horario horario;
}
