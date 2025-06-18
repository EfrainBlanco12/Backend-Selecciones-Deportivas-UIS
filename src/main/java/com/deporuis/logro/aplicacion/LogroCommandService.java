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
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import com.deporuis.seleccion.infraestructura.SeleccionLogroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LogroCommandService {

    @Autowired
    private LogroVerificarExistenciaService logroVerificarExistenciaService;

    @Autowired
    private LogroRepository logroRepository;

    @Autowired
    private LogroRelacionService logroRelacionService;

    @Autowired
    private SeleccionLogroRepository seleccionLogroRepository;

    @Transactional()
    public LogroResponse crearLogro(LogroRequest logroRequest) {
        Logro logro = LogroMapper.requestToLogro(logroRequest);

        List<Seleccion> selecciones =  logroVerificarExistenciaService.verificarSelecciones(logroRequest.getSelecciones());

        logro = logroRepository.save(logro);

        List<SeleccionLogro> relacionesSeleccion = logroRelacionService.crearRelacionesSeleccion(logro, selecciones);

        seleccionLogroRepository.saveAll(relacionesSeleccion);

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

        List<SeleccionLogro> actualesSeleccion = seleccionLogroRepository.findAllByLogro(logro);

        Set<Integer> actualesIdsSeleccion = actualesSeleccion.stream()
                .map(sp -> sp.getSeleccion().getIdSeleccion())
                .collect(Collectors.toSet());
        Set<Integer> nuevasIdsSeleccion = new HashSet<>(idSelecciones);

        List<SeleccionLogro> toRemoveSeleccion = actualesSeleccion.stream()
                .filter(sp -> !nuevasIdsSeleccion.contains(sp.getSeleccion().getIdSeleccion()))
                .collect(Collectors.toList());
        seleccionLogroRepository.deleteAll(toRemoveSeleccion);

        List<Seleccion> seleccionesToAdd = nuevasSelecciones.stream()
                .filter(s -> !actualesIdsSeleccion.contains(s.getIdSeleccion()))
                .collect(Collectors.toList());
        seleccionLogroRepository.saveAll(logroRelacionService.crearRelacionesSeleccion(logro, seleccionesToAdd));

        Logro actualizada = logroRepository.save(logro);
        return LogroMapper.toResponse(actualizada);
    }

    @Transactional()
    public void eliminarLogro(Integer id) {
        Logro logro = logroVerificarExistenciaService.verificarLogro(id);

        seleccionLogroRepository.deleteAll(seleccionLogroRepository.findAllByLogro(logro));

        logroRepository.delete(logro);
    }
}
