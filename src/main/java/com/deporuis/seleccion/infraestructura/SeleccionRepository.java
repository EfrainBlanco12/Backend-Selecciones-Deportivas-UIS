package com.deporuis.seleccion.infraestructura;

import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeleccionRepository extends JpaRepository<Seleccion,Integer> {
    Page<Seleccion> findByVisibilidadTrue(Pageable pageable);
}
