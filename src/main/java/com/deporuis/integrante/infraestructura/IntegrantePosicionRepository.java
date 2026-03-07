package com.deporuis.integrante.infraestructura;

import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrantePosicionRepository extends JpaRepository<IntegrantePosicion, Integer> {
    List<IntegrantePosicion> findAllByIntegrante(Integrante integrante);
}
