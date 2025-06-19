package com.deporuis.horario.aplicacion;

import com.deporuis.horario.aplicacion.mapper.HorarioMapper;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.HorarioRepository;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HorarioCommandService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Transactional()
    public HorarioResponse crearHorario(HorarioRequest horarioRequest) {
        Horario horario = HorarioMapper.requestToHorario(horarioRequest);

        horario = horarioRepository.save(horario);
        return HorarioMapper.toResponse(horario);
    }
}
