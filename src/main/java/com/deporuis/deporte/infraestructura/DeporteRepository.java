package com.deporuis.deporte.infraestructura;

import com.deporuis.deporte.dominio.Deporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeporteRepository extends JpaRepository<Deporte,Integer> {
    Optional<Deporte> findByNombreDeporte(String nombre);
}
