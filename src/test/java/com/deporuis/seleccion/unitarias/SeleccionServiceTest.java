package com.deporuis.seleccion.unitarias;

import com.deporuis.seleccion.aplicacion.SeleccionCommandService;
import com.deporuis.seleccion.aplicacion.SeleccionQueryService;
import com.deporuis.seleccion.aplicacion.SeleccionService;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeleccionServiceTest {

    @InjectMocks
    private SeleccionService seleccionService;

    @Mock
    private SeleccionCommandService seleccionCommandService;

    @Mock
    private SeleccionQueryService seleccionQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearSeleccion_deberiaDelegarEnCommandService() {
        SeleccionRequest request = new SeleccionRequest();
        SeleccionResponse responseEsperado = new SeleccionResponse();
        when(seleccionCommandService.crearSeleccion(request)).thenReturn(responseEsperado);

        SeleccionResponse response = seleccionService.crearSeleccion(request);

        assertEquals(responseEsperado, response);
        verify(seleccionCommandService).crearSeleccion(request);
    }

    @Test
    void obtenerSeleccionesPaginadas_deberiaDelegarEnQueryService() {
        int page = 0, size = 10;
        Page<SeleccionResponse> pagina = new PageImpl<>(List.of(new SeleccionResponse()));
        when(seleccionQueryService.obtenerSeleccionesPaginadas(page, size)).thenReturn(pagina);

        Page<SeleccionResponse> resultado = seleccionService.obtenerSeleccionesPaginadas(page, size);

        assertEquals(pagina, resultado);
        verify(seleccionQueryService).obtenerSeleccionesPaginadas(page, size);
    }

    @Test
    void obtenerSeleccion_deberiaDelegarEnQueryService() {
        int id = 1;
        SeleccionResponse esperado = new SeleccionResponse();
        when(seleccionQueryService.obtenerSeleccion(id)).thenReturn(esperado);

        SeleccionResponse resultado = seleccionService.obtenerSeleccion(id);

        assertEquals(esperado, resultado);
        verify(seleccionQueryService).obtenerSeleccion(id);
    }

    @Test
    void eliminarSeleccion_deberiaDelegarEnCommandService() {
        int id = 1;

        seleccionService.eliminarSeleccion(id);

        verify(seleccionCommandService).eliminarSeleccion(id);
    }

    @Test
    void softDeleteSeleccion_deberiaDelegarEnCommandService() {
        int id = 1;

        seleccionService.softDeleteSeleccion(id);

        verify(seleccionCommandService).softDeleteSeleccion(id);
    }

    @Test
    void actualizarSeleccion_deberiaDelegarEnCommandService() {
        int id = 1;
        SeleccionRequest request = new SeleccionRequest();
        SeleccionResponse esperado = new SeleccionResponse();
        when(seleccionCommandService.actualizarSeleccion(id, request)).thenReturn(esperado);

        SeleccionResponse resultado = seleccionService.actualizarSeleccion(id, request);

        assertEquals(esperado, resultado);
        verify(seleccionCommandService).actualizarSeleccion(id, request);
    }
}
