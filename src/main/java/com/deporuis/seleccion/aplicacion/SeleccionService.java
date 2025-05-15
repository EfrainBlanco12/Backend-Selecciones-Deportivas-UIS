package com.deporuis.seleccion.aplicacion;


import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeleccionService {

    @Autowired
    private SeleccionRepository seleccionRepository;

    public void crearSeleccion(SeleccionDto seleccionDto) {
        Seleccion nuevaSeleccion = new Seleccion();
        nuevaSeleccion.setFechaCreacion(seleccionDto.getFechaCreacion());
        nuevaSeleccion.setNombreSeleccion(seleccionDto.getNombreSeleccion());
        nuevaSeleccion.setEspacioDeportivo(seleccionDto.getEspacioDeportivo());
        nuevaSeleccion.setEquipo(seleccionDto.getEquipo());
        nuevaSeleccion.setIdDeporte(seleccionDto.getIdDeporte());

        seleccionRepository.save(nuevaSeleccion);
    }

}
