package com.deporuis.horario.aplicacion;

import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HorarioService {

    @Autowired
    private HorarioCommandService horarioCommandService;

    @Autowired
    private HorarioQueryService horarioQueryService;

    @Transactional()
    public HorarioResponse crearHorario(HorarioRequest horarioRequest) {
        return horarioCommandService.crearHorario(horarioRequest);
    }

    @Transactional(readOnly = true)
    public HorarioResponse obtenerHorario(Integer id) {
        return horarioQueryService.obtenerHorario(id);
    }

    @Transactional(readOnly = true)
    public Page<HorarioResponse> obtenerHorariosPaginados(Integer page, Integer size) {
        return horarioQueryService.obtenerHorariosPaginados(page, size);
    }

    @Transactional()
    public HorarioResponse actualizarHorario(Integer id, HorarioRequest horarioRequest) {
        return horarioCommandService.actualizarHorario(id, horarioRequest);
    }

    @Transactional()
    public void eliminarHorario(Integer id) {
        horarioCommandService.eliminarHorario(id);
    }
}
