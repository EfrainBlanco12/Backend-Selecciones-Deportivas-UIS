package com.deporuis.logro.aplicacion.helper;

import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.excepciones.LogroNotFoundExcepcion;
import com.deporuis.logro.infraestructura.LogroRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LogroVerificarExistenciaService {

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Autowired
    private LogroRepository logroRepository;

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

    public Logro verificarLogro(Integer id) {
        Optional<Logro> logroOptional = logroRepository.findById(id);

        if (logroOptional.isEmpty()) {
            throw new LogroNotFoundExcepcion("No se encontro logro con ID = " + id);
        }

        return logroOptional.get();
    }
}
