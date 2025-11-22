package com.deporuis.seleccion.infraestructura;

import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeleccionHorarioRepository extends JpaRepository<SeleccionHorario, Integer> {
    List<SeleccionHorario> findAllBySeleccion(Seleccion seleccion);
    List<SeleccionHorario> findAllByHorario(Horario horario);
}
