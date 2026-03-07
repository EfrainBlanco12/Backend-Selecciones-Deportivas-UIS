package com.deporuis.horario.aplicacion.helper;

import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.horario.dominio.DiaHorario;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.excepciones.HorarioNotFoundException;
import com.deporuis.horario.infraestructura.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class HorarioVerificarExistenciaService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Transactional(readOnly = true)
    public Horario verificarHorario(Integer id) {
        Optional<Horario> horarioOptional = horarioRepository.findById(id);

        if (horarioOptional.isEmpty()) {
            throw new HorarioNotFoundException("No se encontro Horario con ID = " + id);
        }

        return horarioOptional.get();
    }

    @Transactional(readOnly = true)
    public List<Horario> verificarHorarios(List<Horario> horarios) {
        List<Integer> idHorarios = horarios.stream().map(Horario::getIdHorario).toList();
        if (idHorarios.isEmpty()) {
            throw new BadRequestException("Debe haber al menos un horario");
        }
        List<Horario> horariosEncontrados = horarioRepository.findAllById(idHorarios);
        if (horariosEncontrados.size() != idHorarios.size()) {
            throw new BadRequestException("Algun horario no existe");
        }
        return horariosEncontrados;
    }

    @Transactional(readOnly = true)
    public List<Horario> verificarHorarioDuplicado(DiaHorario dia, LocalTime horaInicio, LocalTime horaFin) {
        return horarioRepository.findByDiaAndHoraInicioAndHoraFin(dia, horaInicio, horaFin);
    }
}
