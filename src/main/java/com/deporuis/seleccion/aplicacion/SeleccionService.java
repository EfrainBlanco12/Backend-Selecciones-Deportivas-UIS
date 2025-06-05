package com.deporuis.seleccion.aplicacion;


import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeleccionService {

    @Autowired
    private SeleccionRepository seleccionRepository;

    private DeporteRepository deporteRepository;

    public SeleccionResponse crearSeleccion(SeleccionRequest dto) {
        Deporte deporte = deporteRepository.findById(dto.getIdDeporte())
                .orElseThrow(() -> new IllegalArgumentException("El deporte no existe."));

        Seleccion nueva = new Seleccion();
        nueva.setFechaCreacion(dto.getFechaCreacion());
        nueva.setNombreSeleccion(dto.getNombreSeleccion());
        nueva.setEspacioDeportivo(dto.getEspacioDeportivo());
        nueva.setEquipo(dto.getEquipo());
        nueva.setTipo_seleccion(dto.getTipo_seleccion());
        nueva.setDeporte(deporte);

        Seleccion guardada = seleccionRepository.save(nueva);

        return new SeleccionResponse(guardada.getIdSeleccion(), guardada.getNombreSeleccion());
    }


}
