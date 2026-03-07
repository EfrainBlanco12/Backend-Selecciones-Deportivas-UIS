package com.deporuis.horario.aplicacion;

import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.aplicacion.mapper.HorarioMapper;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.HorarioRepository;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HorarioQueryService {

    @Autowired
    private HorarioVerificarExistenciaService horarioVerificarExistenciaService;

    @Autowired
    private HorarioRepository horarioRepository;

    @Transactional(readOnly = true)
    public HorarioResponse obtenerHorario(Integer id) {
        Horario horario = horarioVerificarExistenciaService.verificarHorario(id);
        return HorarioMapper.toResponse(horario);
    }

    @Transactional(readOnly = true)
    public Page<HorarioResponse> obtenerHorariosPaginados(Integer page, Integer size) {
        return horarioRepository.findAll(PageRequest.of(page, size))
                .map(HorarioMapper::toResponse);
    }
}
