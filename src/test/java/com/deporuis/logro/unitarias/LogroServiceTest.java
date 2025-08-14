package com.deporuis.logro.unitarias;

import com.deporuis.logro.aplicacion.LogroCommandService;
import com.deporuis.logro.aplicacion.LogroQueryService;
import com.deporuis.logro.aplicacion.LogroService;
import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LogroServiceTest {

    @InjectMocks private LogroService service;
    @Mock private LogroCommandService command;
    @Mock private LogroQueryService query;

    @BeforeEach void open() { MockitoAnnotations.openMocks(this); }

    private LogroResponse resp() {
        return new LogroResponse(10, LocalDate.of(2024,5,20), "Título Regional", "Torneo", List.of(1,2));
    }

    @Test
    void crear_delegaEnCommand() {
        LogroRequest req = new LogroRequest();
        LogroResponse r = resp();
        when(command.crearLogro(req)).thenReturn(r);
        assertEquals(r, service.crearLogro(req));
        verify(command).crearLogro(req);
    }

    @Test
    void obtener_delegaEnQuery() {
        LogroResponse r = resp();
        when(query.obtenerLogro(5)).thenReturn(r);
        assertEquals(r, service.obtenerLogro(5));
        verify(query).obtenerLogro(5);
    }

    @Test
    void listaPaginada_delegaEnQuery() {
        org.springframework.data.domain.Page<LogroResponse> page =
                new org.springframework.data.domain.PageImpl<>(List.of(resp()));
        when(query.obtenerLogrosPaginados(0,5)).thenReturn(page);
        assertEquals(page, service.obtenerLogrosPaginados(0,5));
        verify(query).obtenerLogrosPaginados(0,5);
    }

    @Test
    void actualizar_eliminar_deleganEnCommand() {
        LogroRequest req = new LogroRequest();
        LogroResponse r = resp();
        when(command.actualizarLogro(3, req)).thenReturn(r);
        assertEquals(r, service.actualizarLogro(3, req));
        verify(command).actualizarLogro(3, req);
        service.eliminarLogro(7);
        verify(command).eliminarLogro(7);
    }
}
