package com.deporuis.integrante.aplicacion;

import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Transactional(readOnly = true)
    public Page<IntegranteResponse> obtenerIntegrantesPaginados(Integer page, Integer size) {
        return integranteQueryService.obtenerIntegrantesPaginados(page, size);
    }

    @Transactional(readOnly = true)
    public IntegranteResponse obtenerIntegrante(Integer id) {
        return integranteQueryService.obtenerIntegrante(id);
    }

    @Transactional()
    public void softDeleteIntegrante(Integer id) {
        integranteCommandService.softDeleteIntegrante(id);
    }

    @Transactional()
    public IntegranteResponse actualizarIntegrante(Integer id, IntegranteRequest integranteRequest) {
        return integranteCommandService.actualizarIntegrante(id, integranteRequest);
    }

    @Transactional(readOnly = true)
    public IntegranteResponse obtenerEntrenadorPorSeleccion(Integer idSeleccion) {
        return integranteQueryService.obtenerEntrenadorPorSeleccion(idSeleccion);
    }

    @Transactional(readOnly = true)
    public long contarIntegrantesPorSeleccion(Integer idSeleccion) {
        return integranteQueryService.contarIntegrantesPorSeleccion(idSeleccion);
    }

    @Transactional(readOnly = true)
    public IntegranteResponse obtenerIntegrantePorCodigoUniversitario(String codigoUniversitario) {
        return integranteQueryService.obtenerIntegrantePorCodigoUniversitario(codigoUniversitario);
    }
}
