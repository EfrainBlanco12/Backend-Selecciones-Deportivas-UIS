package com.deporuis.seleccion.aplicacion;


import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
