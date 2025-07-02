package com.deporuis.integrante.aplicacion;

import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntegranteService {

    @Autowired
    private IntegranteCommandService integranteCommandService;

    @Autowired
    private IntegranteQueryService integranteQueryService;

    @Transactional()
    public IntegranteResponse crearIntegrante(IntegranteRequest integranteRequest) {
        return integranteCommandService.crearIntegrante(integranteRequest);
    }
}
