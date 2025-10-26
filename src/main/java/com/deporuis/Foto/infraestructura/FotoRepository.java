package com.deporuis.Foto.infraestructura;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FotoRepository extends JpaRepository<Foto,Integer> {
    List<Foto> findAllByPublicacion(Publicacion publicacion);

    List<Foto> findAllBySeleccion(Seleccion seleccion);

    List<Foto> findAllByIntegrante(Integrante integrante);

    @Query("SELECT f FROM Foto f WHERE f.seleccion.idSeleccion = :idSeleccion ORDER BY f.idFoto ASC LIMIT 1")
    Optional<Foto> findFirstBySeleccionIdOrderByIdFotoAsc(@Param("idSeleccion") Integer idSeleccion);
}
