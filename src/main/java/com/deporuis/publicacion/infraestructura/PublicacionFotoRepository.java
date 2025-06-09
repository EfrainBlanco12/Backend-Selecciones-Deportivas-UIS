package com.deporuis.publicacion.infraestructura;

import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.PublicacionFoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicacionFotoRepository extends JpaRepository<PublicacionFoto, Integer> {
    List<PublicacionFoto> findAllByPublicacion(Publicacion publicacion);
}
