package com.deporuis.logro.infraestructura;

import com.deporuis.logro.dominio.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogroRepository extends JpaRepository<Logro, Integer> {
}
