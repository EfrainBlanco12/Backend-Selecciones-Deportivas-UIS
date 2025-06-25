package com.deporuis.horario.aplicacion;

import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.aplicacion.mapper.HorarioMapper;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.HorarioRepository;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class HorarioCommandService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private HorarioVerificarExistenciaService horarioVerificarExistenciaService;

    @Transactional()
    public HorarioResponse crearHorario(HorarioRequest horarioRequest) {
        Horario horario = HorarioMapper.requestToHorario(horarioRequest);

        horario = horarioRepository.save(horario);
        return HorarioMapper.toResponse(horario);
    }

    @Transactional()
    public List<Horario> crearHorariosSeleccion(List<HorarioRequest> horarioRequest) {
        List<Horario> nuevosHorarios = HorarioMapper.requestToHorariosSeleccion(horarioRequest);

        return horarioRepository.saveAll(nuevosHorarios);
    }

    @Transactional()
    public void eliminarHorario(Integer id) {
        Horario horario = horarioVerificarExistenciaService.verificarHorario(id);

        horarioRepository.delete(horario);
    }
}