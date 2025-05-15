package com.deporuis.integrante.infraestructura;

import com.deporuis.integrante.dominio.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfraestructuraRepository extends JpaRepository<Integrante, Integer> {
}
