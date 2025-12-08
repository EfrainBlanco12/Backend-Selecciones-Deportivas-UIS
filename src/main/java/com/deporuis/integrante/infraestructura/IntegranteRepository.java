package com.deporuis.integrante.infraestructura;

import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.seleccion.dominio.Seleccion;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Integer> {
    Optional<Integrante> findByCodigoUniversitario(String codigoUniversitario);
    Optional<Integrante> findByCorreoInstitucional(String correoInstitucional);
    Boolean existsByCodigoUniversitario(String codigoUniversitario);
    Boolean existsByCorreoInstitucional(String correoInstitucional);
    Boolean existsByCodigoUniversitarioAndIdIntegranteNot(String codigoUniversitario, Integer id);
    Boolean existsByCorreoInstitucionalAndIdIntegranteNot(String correoInstitucional, Integer id);

    Page<Integrante> findByVisibilidadTrue(Pageable pageable);

    Page<Integrante> findByVisibilidadTrueAndSeleccion_IdSeleccion(Integer idSeleccion, Pageable pageable);

    @Query("SELECT i FROM Integrante i " +
            "JOIN i.rol r " +
            "WHERE i.seleccion.idSeleccion = :idSeleccion " +
            "AND r.idRol = 2")
    Optional<Integrante> findEntrenadorBySeleccionId(@Param("idSeleccion") Integer idSeleccion);

    boolean existsBySeleccion_IdSeleccionAndRol_NombreRolAndVisibilidadTrue(
            Integer idSeleccion,
            String nombreRol
    );

    boolean existsBySeleccion_IdSeleccionAndRol_NombreRolAndVisibilidadTrueAndIdIntegranteNot(
            Integer idSeleccion,
            String nombreRol,
            Integer idIntegrante
    );

    long countBySeleccion_IdSeleccionAndVisibilidadTrue(Integer idSeleccion);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seleccion s where s.idSeleccion = :id")
    Optional<Seleccion> lockById(@Param("id") Integer id);

    @Query("SELECT i FROM Integrante i WHERE EXISTS " +
            "(SELECT 1 FROM Login l WHERE l.codigoUniversitario = i.codigoUniversitario) " +
            "AND i.visibilidad = true " +
            "ORDER BY CASE " +
            "WHEN i.rol.nombreRol = 'ADMINISTRADOR' THEN 1 " +
            "WHEN i.rol.nombreRol = 'ENTRENADOR' THEN 2 " +
            "ELSE 3 END, i.apellidos ASC, i.nombres ASC")
    Page<Integrante> findIntegrantesConLogin(Pageable pageable);
}
