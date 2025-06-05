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
    @Column(name = "codigo_universitario", length = 50)
    private String codigoUniversitario;

    @Column(nullable = false, length = 100)
    private String password;
}



