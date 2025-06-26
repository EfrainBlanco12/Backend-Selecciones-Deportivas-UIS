package com.deporuis.logro.aplicacion.helper;

import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.excepciones.LogroNotFoundExcepcion;
import com.deporuis.logro.infraestructura.LogroRepository;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LogroVerificarExistenciaService {

    @Autowired
    private LogroRepository logroRepository;

    @Autowired
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    public List<Seleccion> verificarSelecciones(List<Integer> idSelecciones) {
        return seleccionVerificarExistenciaService.verificarSelecciones(idSelecciones);
    }

    @Transactional(readOnly = true)
    public Logro verificarLogro(Integer id) {
        Optional<Logro> logroOptional = logroRepository.findById(id);

        if (logroOptional.isEmpty()) {
            throw new LogroNotFoundExcepcion("No se encontro logro con ID = " + id);
        }

        return logroOptional.get();
    }
}
