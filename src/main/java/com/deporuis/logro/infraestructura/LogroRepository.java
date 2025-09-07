package com.deporuis.logro.infraestructura;

import com.deporuis.logro.dominio.Logro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LogroRepository extends JpaRepository<Logro, Integer> {
    @Query(
            value = """
                select l
                from com.deporuis.logro.dominio.Logro l
                join l.selecciones sl
                where sl.seleccion.idSeleccion = :idSeleccion
                order by l.idLogro desc
                """,
            countQuery = """
                select count(l)
                from com.deporuis.logro.dominio.Logro l
                join l.selecciones sl
                where sl.seleccion.idSeleccion = :idSeleccion
                """
    )
    Page<Logro> findLogrosBySeleccion(@Param("idSeleccion") Integer idSeleccion, Pageable pageable);
}
