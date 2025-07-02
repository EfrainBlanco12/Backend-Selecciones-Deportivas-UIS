package com.deporuis.integrante.infraestructura;

import com.deporuis.integrante.dominio.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Integer> {
    Optional<Integrante> findByCodigoUniversitario(String codigoUniversitario);
    Optional<Integrante> findByCorreoInstitucional(String correoInstitucional);
}
