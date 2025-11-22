package com.deporuis.seleccion.aplicacion;


import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPatchRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeleccionService {

    @Autowired
    private SeleccionCommandService seleccionCommandService;

    @Autowired
    private SeleccionQueryService seleccionQueryService;

    @Transactional()
    public SeleccionResponse crearSeleccion(SeleccionRequest request) {
        return seleccionCommandService.crearSeleccion(request);
    }

    @Transactional(readOnly = true)
    public Page<SeleccionResponse> obtenerSeleccionesPaginadas(Integer page, Integer size) {
        return seleccionQueryService.obtenerSeleccionesPaginadas(page, size);
    }

    @Transactional(readOnly = true)
    public SeleccionResponse obtenerSeleccion(Integer id) {
        return seleccionQueryService.obtenerSeleccion(id);
    }

    @Transactional()
    public void eliminarSeleccion(Integer id) {
        seleccionCommandService.eliminarSeleccion(id);
    }

    @Transactional()
    public void softDeleteSeleccion(Integer id) {
        seleccionCommandService.softDeleteSeleccion(id);
    }

    @Transactional()
    public SeleccionResponse actualizarSeleccion(Integer id, SeleccionRequest seleccionRequest) {
        return seleccionCommandService.actualizarSeleccion(id, seleccionRequest);
    }

    @Transactional()
    public SeleccionResponse actualizarSeleccionParcial(Integer id, SeleccionPatchRequest patchRequest) {
        return seleccionCommandService.actualizarSeleccionParcial(id, patchRequest);
    }

    @Transactional(readOnly = true)
    public Page<IntegranteResponse> obtenerIntegrantesSeleccion(Integer idSeleccion, Integer page, Integer size) {
        return seleccionQueryService.obtenerIntegrantesDeSeleccion(idSeleccion, page, size);
    }

    @Transactional(readOnly = true)
    public Page<LogroResponse> obtenerLogrosSeleccion(Integer idSeleccion, Integer page, Integer size) {
        return seleccionQueryService.obtenerLogrosDeSeleccion(idSeleccion, page, size);
    }

    @Transactional(readOnly = true)
    public List<PublicacionResponse> ultimosEventos(Integer idSeleccion) {
        return seleccionQueryService.obtenerUltimos3EventosPorSeleccion(idSeleccion);
    }
}
