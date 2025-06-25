package com.deporuis.horario.aplicacion.helper;

import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.excepciones.HorarioNotFoundException;
import com.deporuis.horario.infraestructura.HorarioRepository;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HorarioVerificarExistenciaService {

    @Autowired
    private HorarioRepository horarioRepository;

    public Horario verificarHorario(Integer id) {
        Optional<Horario> horarioOptional = horarioRepository.findById(id);

        if (horarioOptional.isEmpty()) {
            throw new HorarioNotFoundException("No se encontro Horario con ID = " + id);
        }

        return horarioOptional.get();
    }
}
