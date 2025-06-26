package com.deporuis.Foto.infraestructura;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto,Integer> {
    List<Foto> findAllByPublicacion(Publicacion publicacion);

    List<Foto> findAllBySeleccion(Seleccion seleccion);
}
