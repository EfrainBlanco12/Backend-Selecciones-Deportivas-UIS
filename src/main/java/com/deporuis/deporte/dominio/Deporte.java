package com.deporuis.deporte.dominio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "nombre_deporte", length = 50, nullable = false)
    private String nombreDeporte;

    @Column(name = "descripcion_deporte", length = 500)
    private String descripcionDeporte;

    //Creacion de un deporte
    public Deporte(String nombreDeporte, String descripcionDeporte) {
        this.nombreDeporte = nombreDeporte;
        this.descripcionDeporte = descripcionDeporte;
    }
}

