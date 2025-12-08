package com.deporuis.seleccion.infraestructura;

import com.deporuis.seleccion.dominio.Seleccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeleccionRepository extends JpaRepository<Seleccion,Integer> {

    @EntityGraph(attributePaths = { "deporte" })
    Page<Seleccion> findByVisibilidadTrue(Pageable pageable);

    @EntityGraph(attributePaths = { "deporte", "fotos", "horarios", "horarios.horario" })
    Optional<Seleccion> findById(Integer id);

    boolean existsByIdSeleccion(Integer idSeleccion);

    // Métodos para dashboard
    Long countByVisibilidad(Boolean visibilidad);
    
    Long countByFechaCreacionGreaterThanEqualAndVisibilidad(java.time.LocalDate fecha, Boolean visibilidad);
    
    Long countByFechaCreacionBetweenAndVisibilidad(java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin, Boolean visibilidad);
    
    Long countByFechaCreacionLessThanAndVisibilidad(java.time.LocalDate fecha, Boolean visibilidad);
}

