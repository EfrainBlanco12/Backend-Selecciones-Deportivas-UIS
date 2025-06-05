package com.deporuis.auth.infraestructura;

import com.deporuis.auth.dominio.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login, String> {
    Optional<Login> findByCodigoUniversitario(String codigoUniversitario);
}
