package com.deporuis.deporte.dominio;

import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.posicion.dominio.Posicion;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Deportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_deporte")
    private Integer idDeporte;

    @Column(name = "nombre_deporte", length = 50)
    private String nombreDeporte;

    @Column(name = "descripcion_deporte", length = 500)
    private String descripcionDeporte;

    // Inversa de Seleccion → Muchos Selecciones por un Deporte
    @OneToMany(mappedBy = "deporte")
    private List<Seleccion> selecciones;

    // Inversa de Posicion → Muchas Posiciones por un Deporte
    @OneToMany(mappedBy = "deporte")
    private List<Posicion> posiciones;

    public Deporte(String nombreDeporte, String descripcionDeporte) {
        this.nombreDeporte = nombreDeporte;
        this.descripcionDeporte = descripcionDeporte;
    }
}
