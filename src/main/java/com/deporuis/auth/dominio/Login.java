package com.deporuis.auth.dominio;

import com.deporuis.integrante.dominio.Integrante;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Login")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Login {

    @Id
    @Column(name = "id_integrante")
    private Integer idIntegrante;

    @Column(nullable = false, length = 100)
    private String password;

    // Relación con la tabla Integrantes (FK y PK al mismo tiempo)
    @OneToOne
    @MapsId
    @JoinColumn(name = "id_integrante")
    private Integrante integrante;
}

