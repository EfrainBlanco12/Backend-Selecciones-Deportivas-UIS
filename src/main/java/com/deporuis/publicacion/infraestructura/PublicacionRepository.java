package com.deporuis.publicacion.infraestructura;

import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Integer> {
    Page<Publicacion> findByVisibilidadTrue(Pageable pageable);

    Page<Publicacion> findByVisibilidadTrueAndTipoPublicacion(TipoPublicacion tipo, Pageable pageable);

    Optional<Publicacion> findByIdPublicacionAndVisibilidadTrue(Integer idPublicacion);

    @Query("""
           select s.idSeleccion
           from com.deporuis.seleccion.dominio.SeleccionPublicacion sp
           join sp.seleccion s
           where sp.publicacion.idPublicacion = :pubId
           """)
    List<Integer> findSeleccionIdsByPublicacionId(@Param("pubId") Integer pubId);

    @Query("""
           select new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(s.idSeleccion, s.nombreSeleccion)
           from com.deporuis.seleccion.dominio.SeleccionPublicacion sp
           join sp.seleccion s
           where sp.publicacion.idPublicacion = :pubId
           """)
    List<SeleccionPublicacionResponse> findSeleccionDtosByPublicacionId(@Param("pubId") Integer pubId);

    @Query("""
        SELECT p
        FROM Publicacion p
        JOIN SeleccionPublicacion sp ON sp.publicacion.id = p.id
        WHERE sp.seleccion.id = :idSeleccion
          AND p.tipoPublicacion = :tipo
          AND p.visibilidad = true
        ORDER BY p.fecha DESC
    """)
    Page<Publicacion> findBySeleccionAndTipoOrderByFechaDesc(
            @Param("idSeleccion") Integer idSeleccion,
            @Param("tipo") TipoPublicacion tipo,
            Pageable pageable
    );
}
