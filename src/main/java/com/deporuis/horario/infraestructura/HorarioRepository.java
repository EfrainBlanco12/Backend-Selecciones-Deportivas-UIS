package com.deporuis.horario.infraestructura;

import com.deporuis.horario.dominio.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario,Integer> {
}
