package com.deporuis.publicacion.infraestructura;

import com.deporuis.publicacion.dominio.Publicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Integer> {
    Page<Publicacion> findByVisibilidadTrue(Pageable pageable);
}
