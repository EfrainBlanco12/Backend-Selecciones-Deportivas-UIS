package com.deporuis.integrante.aplicacion;

import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
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
}
