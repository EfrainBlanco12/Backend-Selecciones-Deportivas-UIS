package com.deporuis.seleccion.infraestructura;

import com.deporuis.seleccion.dominio.SeleccionHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeleccionHorarioRepository extends JpaRepository<SeleccionHorario, Integer> {
}
