package com.deporuis.seleccion.unitarias;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.TipoSeleccion;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SeleccionMapperTest {

    @Test
    void requestToSeleccion_debeConvertirCorrectamente() {
        SeleccionRequest request = new SeleccionRequest();
        request.setFechaCreacion(LocalDate.of(2024, 1, 1));
        request.setNombreSeleccion("Fútbol Masculino");
        request.setEspacioDeportivo("Cancha Central");
        request.setEquipo(true);
        request.setTipo_seleccion(TipoSeleccion.MASCULINO);
        request.setIdDeporte(1);

        Seleccion seleccion = SeleccionMapper.requestToSeleccion(request);

        assertNotNull(seleccion);
        assertEquals(LocalDate.of(2024, 1, 1), seleccion.getFechaCreacion());
        assertEquals("Fútbol Masculino", seleccion.getNombreSeleccion());
        assertEquals("Cancha Central", seleccion.getEspacioDeportivo());
        assertEquals(true, seleccion.getEquipo());
        assertEquals(TipoSeleccion.MASCULINO, seleccion.getTipo_seleccion());
    }

    @Test
    void seleccionToResponse_debeConvertirCorrectamente() {
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(1);
        seleccion.setFechaCreacion(LocalDate.of(2024, 1, 1));
        seleccion.setNombreSeleccion("Fútbol Masculino");
        seleccion.setEspacioDeportivo("Cancha Central");
        seleccion.setEquipo(true);
        seleccion.setTipo_seleccion(TipoSeleccion.MASCULINO);

        Deporte deporte = new Deporte();
        deporte.setIdDeporte(1);
        deporte.setNombreDeporte("Fútbol");
        seleccion.setDeporte(deporte);
        
        seleccion.setFotos(new ArrayList<>());
        seleccion.setHorarios(new ArrayList<>());
        seleccion.setLogros(new ArrayList<>());

        SeleccionResponse response = SeleccionMapper.seleccionToResponse(seleccion);

        assertNotNull(response);
        assertEquals(1, response.getIdSeleccion());
        assertEquals("Fútbol Masculino", response.getNombreSeleccion());
        assertEquals("Cancha Central", response.getEspacioDeportivo());
        assertEquals(true, response.getEquipo());
        assertEquals(TipoSeleccion.MASCULINO, response.getTipoSeleccion());
        assertNotNull(response.getDeporte());
        assertEquals(1, response.getDeporte().getIdDeporte());
        assertEquals("Fútbol", response.getDeporte().getNombreDeporte());
    }

    @Test
    void seleccionToResponse_sinDeporte_debeRetornarNull() {
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(1);
        seleccion.setNombreSeleccion("Sin Deporte");
        seleccion.setFotos(new ArrayList<>());
        seleccion.setHorarios(new ArrayList<>());
        seleccion.setLogros(new ArrayList<>());

        SeleccionResponse response = SeleccionMapper.seleccionToResponse(seleccion);

        assertNotNull(response);
        assertNull(response.getDeporte());
    }
}
