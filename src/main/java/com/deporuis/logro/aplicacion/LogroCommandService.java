package com.deporuis.logro.aplicacion;

import com.deporuis.logro.aplicacion.helper.LogroRelacionService;
import com.deporuis.logro.aplicacion.helper.LogroVerificarExistenciaService;
import com.deporuis.logro.aplicacion.mapper.LogroMapper;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.infraestructura.LogroRepository;
import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionLogro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LogroCommandService {

    @Autowired
    private LogroVerificarExistenciaService logroVerificarExistenciaService;

    @Autowired
    private LogroRepository logroRepository;

    @Autowired
    private LogroRelacionService logroRelacionService;

    @Transactional()
    public LogroResponse crearLogro(LogroRequest logroRequest) {
        Logro logro = LogroMapper.requestToLogro(logroRequest);

        List<Seleccion> selecciones =  logroVerificarExistenciaService.verificarSelecciones(logroRequest.getSelecciones());

        logro = logroRepository.save(logro);

        List<SeleccionLogro> relacionesSeleccion = logroRelacionService.crearRelacionesSeleccion(logro, selecciones);

        logro.setSelecciones(relacionesSeleccion);

        return LogroMapper.toResponse(logro);
    }

    @Transactional()
    public LogroResponse actualizarLogro(Integer id, LogroRequest logroRequest) {
        Logro logro = logroVerificarExistenciaService.verificarLogro(id);

        logro.setFechaLogro(logroRequest.getFechaLogro());
        logro.setNombreLogro(logroRequest.getNombreLogro());
        logro.setDescripcionLogro(logroRequest.getDescripcionLogro());

        List<Integer> idSelecciones = logroRequest.getSelecciones();
        List<Seleccion> nuevasSelecciones = logroVerificarExistenciaService.verificarSelecciones(idSelecciones);
        List<SeleccionLogro> relacionesSeleccion = logroRelacionService.actualizarRelacionesSeleccion(logro, nuevasSelecciones, idSelecciones);

        logro.setSelecciones(relacionesSeleccion);

        Logro actualizada = logroRepository.save(logro);
        return LogroMapper.toResponse(actualizada);
    }

    @Transactional()
    public void eliminarLogro(Integer id) {
        Logro logro = logroVerificarExistenciaService.verificarLogro(id);

        logroRelacionService.eliminarRelacionesSeleccion(logro);

        logroRepository.delete(logro);
    }
}
