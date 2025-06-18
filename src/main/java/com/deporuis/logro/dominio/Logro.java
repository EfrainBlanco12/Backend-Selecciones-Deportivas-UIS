package com.deporuis.logro.dominio;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Logros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_logro")
    private Integer idLogro;

    @Column(name = "fecha_logro", nullable = false)
    private LocalDate fechaLogro;

    @Column(name = "nombre_logro", length = 100, nullable = false)
    private String nombreLogro;

    @Column(name = "descripcion_logro", length = 500)
    private String descripcionLogro;

    public Logro(LocalDate fechaLogro, String nombreLogro, String descripcionLogro) {
        this.fechaLogro = fechaLogro;
        this.nombreLogro = nombreLogro;
        this.descripcionLogro = descripcionLogro;
    }
}
