package com.deporuis.integrante.infraestructura;

import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Integer> {
    Optional<Integrante> findByCodigoUniversitario(String codigoUniversitario);
    Optional<Integrante> findByCorreoInstitucional(String correoInstitucional);
    Boolean existsByCodigoUniversitarioAndIdIntegranteNot(String codigoUniversitario, Integer id);
    Boolean existsByCorreoInstitucionalAndIdIntegranteNot(String correoInstitucional, Integer id);


    Page<Integrante> findByVisibilidadTrue(Pageable pageable);
}
