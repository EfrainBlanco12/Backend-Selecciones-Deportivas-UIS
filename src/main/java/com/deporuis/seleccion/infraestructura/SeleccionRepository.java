package com.deporuis.seleccion.infraestructura;

import com.deporuis.seleccion.dominio.Seleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeleccionRepository extends JpaRepository<Seleccion,Integer> {

}
