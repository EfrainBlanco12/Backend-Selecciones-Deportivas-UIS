package com.deporuis.logro.aplicacion;

import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogroService {

    @Autowired
    private LogroCommandService logroCommandService;

    @Autowired
    private LogroQueryService logroQueryService;

    @Transactional()
    public LogroResponse crearLogro(LogroRequest logroRequest) {
        return logroCommandService.crearLogro(logroRequest);
    }

    @Transactional(readOnly = true)
    public Page<LogroResponse> obtenerLogrosPaginados(Integer page, Integer size) {
        return logroQueryService.obtenerLogrosPaginados(page, size);
    }

    @Transactional(readOnly = true)
    public LogroResponse obtenerLogro(Integer id) {
        return logroQueryService.obtenerLogro(id);
    }

    @Transactional()
    public LogroResponse actualizarLogro(Integer id, LogroRequest logroRequest) {
        return logroCommandService.actualizarLogro(id, logroRequest);
    }

    @Transactional()
    public void eliminarLogro(Integer id) {
        logroCommandService.eliminarLogro(id);
    }
}
