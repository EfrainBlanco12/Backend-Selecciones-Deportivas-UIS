package com.deporuis.integrante.aplicacion;

import com.deporuis.excepcion.common.ResourceNotFoundException;
import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntegranteQueryService {

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;
    @Autowired
    private IntegranteVerificarExistenciaService integranteVerificarExistenciaService;
    @Transactional(readOnly = true)
    public Page<IntegranteResponse> obtenerIntegrantesPaginados(Integer page, Integer size) {
        return integranteRepository.findByVisibilidadTrue(PageRequest.of(page, size))
                .map(IntegranteMapper::integranteToResponse);
    }

    @Transactional(readOnly = true)
    public IntegranteResponse obtenerIntegrante(Integer id) {
        Integrante integrante = integranteVerificarExistenciaService.verificarIntegrante(id);
        return IntegranteMapper.integranteToResponse(integrante);
    }

    @Transactional(readOnly = true)
    public IntegranteResponse obtenerEntrenadorPorSeleccion(Integer idSeleccion) {
        integranteVerificarExistenciaService.verificarSeleccion(idSeleccion);
        var entrenador = integranteRepository.findEntrenadorBySeleccionId(idSeleccion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La selección con id " + idSeleccion + " no tiene un entrenador asignado"
                ));

        return IntegranteMapper.toResponse(entrenador);
    }

    @Transactional(readOnly = true)
    public long contarIntegrantesPorSeleccion(Integer idSeleccion) {
        seleccionVerificarExistenciaService.verificarSeleccion(idSeleccion);
        return integranteRepository.countBySeleccion_IdSeleccionAndVisibilidadTrue(idSeleccion);
    }

    @Transactional(readOnly = true)
    public IntegranteResponse obtenerIntegrantePorCodigoUniversitario(String codigoUniversitario) {
        Integrante integrante = integranteRepository.findByCodigoUniversitario(codigoUniversitario)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró integrante con código universitario: " + codigoUniversitario
                ));
        return IntegranteMapper.integranteToResponse(integrante);
    }
}
