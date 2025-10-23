package com.deporuis.horario.aplicacion.helper;

import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.infraestructura.SeleccionHorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HorarioRelacionService {

    @Autowired
    private SeleccionHorarioRepository seleccionHorarioRepository;

    @Autowired
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    @Transactional()
    public List<SeleccionHorario> crearRelacionesConSelecciones(Horario horario, List<Integer> idSelecciones) {
        if (idSelecciones == null || idSelecciones.isEmpty()) {
            return List.of();
        }

        return seleccionHorarioRepository.saveAll(
                idSelecciones.stream()
                        .map(idSeleccion -> {
                            Seleccion seleccion = seleccionVerificarExistenciaService.verificarSeleccion(idSeleccion);
                            SeleccionHorario sh = new SeleccionHorario();
                            sh.setSeleccion(seleccion);
                            sh.setHorario(horario);
                            return sh;
                        })
                        .toList()
        );
    }
}
