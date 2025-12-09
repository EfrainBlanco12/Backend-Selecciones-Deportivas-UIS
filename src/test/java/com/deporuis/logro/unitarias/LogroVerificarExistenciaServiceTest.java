package com.deporuis.logro.unitarias;

import com.deporuis.logro.aplicacion.helper.LogroVerificarExistenciaService;
import com.deporuis.logro.excepciones.LogroNotFoundExcepcion;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.infraestructura.LogroRepository;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogroVerificarExistenciaServiceTest {

    @InjectMocks
    private LogroVerificarExistenciaService service;

    @Mock
    private LogroRepository logroRepository;

    @Mock
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void verificarLogro_cuandoExiste_debeRetornarLogro() {
        Logro logro = new Logro();
        logro.setIdLogro(1);
        when(logroRepository.findById(1)).thenReturn(Optional.of(logro));

        Logro result = service.verificarLogro(1);

        assertNotNull(result);
        assertEquals(1, result.getIdLogro());
        verify(logroRepository).findById(1);
    }

    @Test
    void verificarLogro_cuandoNoExiste_debeLanzarExcepcion() {
        when(logroRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(LogroNotFoundExcepcion.class, () -> service.verificarLogro(1));
        verify(logroRepository).findById(1);
    }

    @Test
    void verificarSelecciones_debeVerificarTodasLasSelecciones() {
        Seleccion sel1 = new Seleccion();
        sel1.setIdSeleccion(1);
        Seleccion sel2 = new Seleccion();
        sel2.setIdSeleccion(2);
        List<Seleccion> selecciones = List.of(sel1, sel2);

        when(seleccionVerificarExistenciaService.verificarSelecciones(List.of(1, 2))).thenReturn(selecciones);

        List<Seleccion> result = service.verificarSelecciones(List.of(1, 2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getIdSeleccion());
        assertEquals(2, result.get(1).getIdSeleccion());
        verify(seleccionVerificarExistenciaService).verificarSelecciones(List.of(1, 2));
    }
}
