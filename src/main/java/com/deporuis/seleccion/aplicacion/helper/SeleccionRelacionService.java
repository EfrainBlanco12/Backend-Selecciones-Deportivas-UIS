package com.deporuis.seleccion.aplicacion.helper;

import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.infraestructura.SeleccionHorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeleccionRelacionService {

    @Autowired
    private SeleccionHorarioRepository seleccionHorarioRepository;

    public List<SeleccionHorario> crearRelacionesHorarios(Seleccion seleccion, List<Horario> horarios) {
        return seleccionHorarioRepository.saveAll(
                horarios.stream()
                        .map(
                                horario -> {
                                    SeleccionHorario sh = new SeleccionHorario();
                                    sh.setSeleccion(seleccion);
                                    sh.setHorario(horario);
                                    return sh;
                                }
                        )
                        .toList()
        );
    }
}
