package com.deporuis.horario.aplicacion;

import com.deporuis.horario.aplicacion.helper.HorarioRelacionService;
import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.aplicacion.mapper.HorarioMapper;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.HorarioRepository;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.infraestructura.SeleccionHorarioRepository;
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

    @Autowired
    private HorarioRelacionService horarioRelacionService;

    @Autowired
    private SeleccionHorarioRepository seleccionHorarioRepository;

    @Transactional()
    public HorarioResponse crearHorario(HorarioRequest horarioRequest) {
        Horario horario = HorarioMapper.requestToHorario(horarioRequest);

        horario = horarioRepository.save(horario);

        // Crear relaciones con selecciones si se proporcionaron IDs
        if (horarioRequest.getIdSelecciones() != null && !horarioRequest.getIdSelecciones().isEmpty()) {
            List<SeleccionHorario> relaciones = horarioRelacionService.crearRelacionesConSelecciones(
                    horario,
                    horarioRequest.getIdSelecciones()
            );
            horario.setSelecciones(relaciones);
        }

        return HorarioMapper.toResponse(horario);
    }

    @Transactional()
    public List<Horario> obtenerOcrearHorariosSeleccion(List<HorarioRequest> horarioRequest) {
        List<Horario> horarios = HorarioMapper.requestToHorariosSeleccion(horarioRequest);

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
    public HorarioResponse actualizarHorario(Integer id, HorarioRequest horarioRequest) {
        Horario horario = horarioVerificarExistenciaService.verificarHorario(id);

        horario.setDia(horarioRequest.getDia());
        horario.setHoraInicio(horarioRequest.getHoraInicio());
        horario.setHoraFin(horarioRequest.getHoraFin());

        // Actualizar relaciones con selecciones si se proporcionaron IDs
        if (horarioRequest.getIdSelecciones() != null && !horarioRequest.getIdSelecciones().isEmpty()) {
            // Eliminar relaciones existentes del horario (necesitamos implementar esto)
            // Por ahora creamos las nuevas relaciones
            List<SeleccionHorario> relaciones = horarioRelacionService.crearRelacionesConSelecciones(
                    horario,
                    horarioRequest.getIdSelecciones()
            );
            horario.setSelecciones(relaciones);
        }

        Horario horarioActualizado = horarioRepository.save(horario);

        return HorarioMapper.toResponse(horarioActualizado);
    }

    @Transactional()
    public void eliminarHorario(Integer id) {
        Horario horario = horarioVerificarExistenciaService.verificarHorario(id);
        
        // Eliminar primero las relaciones con selecciones
        List<SeleccionHorario> relaciones = seleccionHorarioRepository.findAllByHorario(horario);
        if (!relaciones.isEmpty()) {
            seleccionHorarioRepository.deleteAll(relaciones);
        }
        
        // Ahora eliminar el horario
        horarioRepository.delete(horario);
    }
}