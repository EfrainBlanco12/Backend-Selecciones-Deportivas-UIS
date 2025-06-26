package com.deporuis.seleccion.aplicacion;

import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SeleccionQueryService {

    @Autowired
    private SeleccionRepository seleccionRepository;
    public Page<SeleccionResponse> obtenerSeleccionesPaginadas(Integer page, Integer size) {
        return seleccionRepository.findByVisibilidadTrue(PageRequest.of(page, size))
                .map(SeleccionMapper::seleccionToResponse);
    }
}
