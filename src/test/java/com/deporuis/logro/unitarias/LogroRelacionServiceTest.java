package com.deporuis.logro.unitarias;

import com.deporuis.logro.aplicacion.helper.LogroRelacionService;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionLogro;
import com.deporuis.seleccion.infraestructura.SeleccionLogroRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

class LogroRelacionServiceTest {

    @InjectMocks
    private LogroRelacionService service;

    @Mock
    private SeleccionLogroRepository repository;

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
    void crearRelacionesSeleccion_debeCrearRelacionesCorrectamente() {
        Logro logro = new Logro();
        logro.setIdLogro(1);

        Seleccion sel1 = new Seleccion();
        sel1.setIdSeleccion(1);
        Seleccion sel2 = new Seleccion();
        sel2.setIdSeleccion(2);
        List<Seleccion> selecciones = List.of(sel1, sel2);

        List<SeleccionLogro> expected = List.of(new SeleccionLogro(), new SeleccionLogro());
        when(repository.saveAll(anyList())).thenReturn(expected);

        List<SeleccionLogro> result = service.crearRelacionesSeleccion(logro, selecciones);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).saveAll(anyList());
    }

    @Test
    void actualizarRelacionesSeleccion_debeActualizarCorrectamente() {
        Logro logro = new Logro();
        logro.setIdLogro(1);

        Seleccion sel1 = new Seleccion();
        sel1.setIdSeleccion(1);
        Seleccion sel2 = new Seleccion();
        sel2.setIdSeleccion(2);
        Seleccion sel3 = new Seleccion();
        sel3.setIdSeleccion(3);

        SeleccionLogro existing1 = new SeleccionLogro();
        existing1.setSeleccion(sel1);
        SeleccionLogro existing2 = new SeleccionLogro();
        existing2.setSeleccion(sel2);

        List<SeleccionLogro> actuales = List.of(existing1, existing2);
        when(repository.findAllByLogro(logro)).thenReturn(actuales);

        List<Seleccion> nuevasSelecciones = List.of(sel2, sel3);
        List<Integer> nuevosIds = List.of(2, 3);

        doNothing().when(repository).deleteAll(anyList());
        when(repository.saveAll(anyList())).thenReturn(new ArrayList<>());

        List<SeleccionLogro> result = service.actualizarRelacionesSeleccion(logro, nuevasSelecciones, nuevosIds);

        assertNotNull(result);
        verify(repository, atLeastOnce()).findAllByLogro(logro);
        verify(repository).deleteAll(anyList());
        verify(repository, atLeastOnce()).saveAll(anyList());
    }

    @Test
    void eliminarRelacionesSeleccion_debeEliminarTodasLasRelaciones() {
        Logro logro = new Logro();
        logro.setIdLogro(1);

        List<SeleccionLogro> relaciones = List.of(new SeleccionLogro(), new SeleccionLogro());
        when(repository.findAllByLogro(logro)).thenReturn(relaciones);
        doNothing().when(repository).deleteAll(relaciones);

        service.eliminarRelacionesSeleccion(logro);

        verify(repository).findAllByLogro(logro);
        verify(repository).deleteAll(relaciones);
    }
}
