package com.deporuis.seleccion.aplicacion;


import com.deporuis.deporte.aplicacion.helper.DeporteVerificarExistenciaService;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeleccionService {

    @Autowired
    private SeleccionCommandService seleccionCommandService;

    @Transactional()
    public SeleccionResponse crearSeleccion(SeleccionRequest request) {
        return seleccionCommandService.crearSeleccion(request);
    }


}
