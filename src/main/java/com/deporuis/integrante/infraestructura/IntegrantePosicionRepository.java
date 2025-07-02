package com.deporuis.integrante.infraestructura;

import com.deporuis.integrante.dominio.IntegrantePosicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrantePosicionRepository extends JpaRepository<IntegrantePosicion, Integer> {
}
