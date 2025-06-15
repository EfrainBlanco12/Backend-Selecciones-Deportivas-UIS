package com.deporuis.publicacion.aplicacion;

import com.deporuis.publicacion.aplicacion.helper.PublicacionVerificarExistenciaService;
import com.deporuis.publicacion.aplicacion.mapper.PublicacionMapper;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicacionQueryService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private PublicacionVerificarExistenciaService verificarExistenciaService;

    @Transactional(readOnly = true)
    public PublicacionResponse obtenerPublicacion(Integer id) {
        Publicacion publicacion = verificarExistenciaService.verificarPublicacion(id);
        return PublicacionMapper.toResponse(publicacion);
    }

    @Transactional(readOnly = true)
    public Page<PublicacionResponse> obtenerPublicacionesPaginadas(int page, int size) {
        return publicacionRepository.findByVisibilidadTrue(PageRequest.of(page, size))
                .map(PublicacionMapper::toResponse);
    }
}
