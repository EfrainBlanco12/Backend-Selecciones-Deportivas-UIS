package com.deporuis.publicacion.infraestructura;

import com.deporuis.publicacion.dominio.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Integer> {

}
