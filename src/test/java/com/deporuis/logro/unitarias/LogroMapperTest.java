package com.deporuis.logro.unitarias;

import com.deporuis.logro.aplicacion.mapper.LogroMapper;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LogroMapperTest {

    @Test
    void requestToLogro_debeConvertirCorrectamente() {
        LogroRequest request = new LogroRequest();
        request.setFechaLogro(LocalDate.of(2024, 5, 20));
        request.setNombreLogro("Campeón Regional");
        request.setDescripcionLogro("Primer lugar en torneo regional");

        Logro logro = LogroMapper.requestToLogro(request);

        assertNotNull(logro);
        assertEquals(LocalDate.of(2024, 5, 20), logro.getFechaLogro());
        assertEquals("Campeón Regional", logro.getNombreLogro());
        assertEquals("Primer lugar en torneo regional", logro.getDescripcionLogro());
    }

    @Test
    void toResponse_debeConvertirCorrectamente() {
        Logro logro = new Logro();
        logro.setIdLogro(1);
        logro.setFechaLogro(LocalDate.of(2024, 5, 20));
        logro.setNombreLogro("Campeón Regional");
        logro.setDescripcionLogro("Primer lugar en torneo regional");
        logro.setSelecciones(new ArrayList<>());

        LogroResponse response = LogroMapper.toResponse(logro);

        assertNotNull(response);
        assertEquals(1, response.getIdLogro());
        assertEquals(LocalDate.of(2024, 5, 20), response.getFechaLogro());
        assertEquals("Campeón Regional", response.getNombreLogro());
        assertEquals("Primer lugar en torneo regional", response.getDescripcionLogro());
        assertNotNull(response.getSelecciones());
        assertTrue(response.getSelecciones().isEmpty());
    }
}
