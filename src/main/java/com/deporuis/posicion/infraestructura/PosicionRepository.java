package com.deporuis.posicion.infraestructura;

import com.deporuis.posicion.dominio.Posicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PosicionRepository extends JpaRepository<Posicion, Integer> {
    List<Posicion> findAllByDeporte_IdDeporteAndVisibilidadTrue(Integer idDeporte);
    Optional<Posicion> findByIdPosicionAndVisibilidadTrue(Integer idPosicion);
    List<Posicion> findAllByDeporte_IdDeporte(Integer idDeporte);

}
