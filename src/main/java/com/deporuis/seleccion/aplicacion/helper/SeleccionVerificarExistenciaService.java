package com.deporuis.seleccion.aplicacion.helper;

import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeleccionVerificarExistenciaService {

    @Autowired
    private SeleccionRepository seleccionRepository;

    public List<Seleccion> verificarSelecciones(List<Integer> idSelecciones) {
        if (idSelecciones.isEmpty()) {
            throw new BadRequestException("Debe haber al menos una seleccion");
        }
        List<Seleccion> selecciones = seleccionRepository.findAllById(idSelecciones);
        if (selecciones.size() != idSelecciones.size()) {
            throw new BadRequestException("Alguna seleccion no existe");
        }
        return selecciones;
    }
}
