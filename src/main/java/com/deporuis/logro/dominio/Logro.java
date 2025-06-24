package com.deporuis.logro.dominio;

import com.deporuis.seleccion.dominio.SeleccionLogro;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "logro")
    private List<SeleccionLogro> selecciones = new ArrayList<>();

    public Logro(LocalDate fechaLogro, String nombreLogro, String descripcionLogro) {
        this.fechaLogro = fechaLogro;
        this.nombreLogro = nombreLogro;
        this.descripcionLogro = descripcionLogro;
    }
}
