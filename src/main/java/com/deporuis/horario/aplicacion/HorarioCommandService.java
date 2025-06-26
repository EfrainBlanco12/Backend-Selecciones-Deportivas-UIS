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

import java.util.ArrayList;
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
    public List<Horario> obtenerOcrearHorariosSeleccion(List<HorarioRequest> horarioRequest) {
        List<Horario> horarios = HorarioMapper.requestToHorariosSeleccion(horarioRequest);
//        return horarioRepository.saveAll(horarios);
        List<Horario> resultado = new ArrayList<>();

        for (Horario horario : horarios) {
            List<Horario> existente = horarioVerificarExistenciaService.verificarHorarioDuplicado(
                    horario.getDia(),
                    horario.getHoraInicio(),
                    horario.getHoraFin()
            );

            if (!existente.isEmpty()) {
                resultado.add(existente.get(0));
            } else {
                Horario nuevo = new Horario();
                nuevo.setDia(horario.getDia());
                nuevo.setHoraInicio(horario.getHoraInicio());
                nuevo.setHoraFin(horario.getHoraFin());

                Horario guardado = horarioRepository.save(nuevo);
                resultado.add(guardado);
            }
        }
        return resultado;
    }

    @Transactional()
    public void eliminarHorario(Integer id) {
        Horario horario = horarioVerificarExistenciaService.verificarHorario(id);

        horarioRepository.delete(horario);
    }
}