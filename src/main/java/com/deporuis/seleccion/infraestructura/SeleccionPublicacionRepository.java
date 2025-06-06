package com.deporuis.seleccion.infraestructura;

import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeleccionPublicacionRepository extends JpaRepository<SeleccionPublicacion, Integer> {
    SeleccionPublicacion findByPublicacion(Publicacion publicacion);
    List<SeleccionPublicacion> findAllByPublicacion(Publicacion publicacion);
}
