package com.deporuis.logro.aplicacion;

import com.deporuis.logro.aplicacion.helper.LogroVerificarExistenciaService;
import com.deporuis.logro.aplicacion.mapper.LogroMapper;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.infraestructura.LogroRepository;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogroQueryService {

    @Autowired
    private LogroVerificarExistenciaService logroVerificarExistenciaService;

    @Autowired
    private LogroRepository logroRepository;

    @Transactional(readOnly = true)
    public LogroResponse obtenerLogro(Integer id) {
        Logro logro = logroVerificarExistenciaService.verificarLogro(id);
        return LogroMapper.toResponse(logro);
    }

    @Transactional(readOnly = true)
    public Page<LogroResponse> obtenerLogrosPaginados(Integer page, Integer size) {
        return logroRepository.findAll(PageRequest.of(page, size))
                .map(LogroMapper::toResponse);
    }
}
