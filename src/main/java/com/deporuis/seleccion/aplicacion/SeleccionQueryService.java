package com.deporuis.seleccion.aplicacion;

import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeleccionQueryService {

    @Autowired
    private SeleccionRepository seleccionRepository;

    @Autowired
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    @Transactional(readOnly = true)
    public Page<SeleccionResponse> obtenerSeleccionesPaginadas(Integer page, Integer size) {
        // findByVisibilidadTrue tiene @EntityGraph: deporte, fotos, horarios.horario
        return seleccionRepository.findByVisibilidadTrue(PageRequest.of(page, size))
                .map(SeleccionMapper::seleccionToResponse);
    }

    @Transactional(readOnly = true)
    public SeleccionResponse obtenerSeleccion(Integer id) {
        // verificarSeleccion usa el repository; con nuestro @EntityGraph en findById,
        // las relaciones están cargadas para el mapper.
        Seleccion seleccion = seleccionVerificarExistenciaService.verificarSeleccion(id);
        return SeleccionMapper.seleccionToResponse(seleccion);
    }
}
