package com.deporuis.horario.infraestructura;

import com.deporuis.horario.dominio.DiaHorario;
import com.deporuis.horario.dominio.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,Integer> {
    List<Horario> findByDiaAndHoraInicioAndHoraFin(DiaHorario dia, LocalTime horaInicio, LocalTime horaFin);
}
