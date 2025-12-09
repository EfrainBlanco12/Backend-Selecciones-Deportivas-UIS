package com.deporuis.integrante.unitarias;

import com.deporuis.integrante.aplicacion.helper.IntegranteRelacionService;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
import com.deporuis.integrante.infraestructura.IntegrantePosicionRepository;
import com.deporuis.posicion.dominio.Posicion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class IntegranteRelacionServiceTest {

    @InjectMocks
    private IntegranteRelacionService service;

    @Mock
    private IntegrantePosicionRepository repository;

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
    void crearRelacionesPosicion_debeCrearRelacionesCorrectamente() {
        Integrante integrante = new Integrante();
        integrante.setIdIntegrante(1);

        Posicion pos1 = new Posicion();
        pos1.setIdPosicion(1);
        Posicion pos2 = new Posicion();
        pos2.setIdPosicion(2);
        List<Posicion> posiciones = List.of(pos1, pos2);

        List<IntegrantePosicion> expected = List.of(new IntegrantePosicion(), new IntegrantePosicion());
        when(repository.saveAll(anyList())).thenReturn(expected);

        List<IntegrantePosicion> result = service.crearRelacionesPosicion(integrante, posiciones);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository).saveAll(anyList());
    }

    @Test
    void eliminarRelacionesPosicion_debeEliminarTodasLasRelaciones() {
        Integrante integrante = new Integrante();
        integrante.setIdIntegrante(1);

        List<IntegrantePosicion> relaciones = List.of(new IntegrantePosicion(), new IntegrantePosicion());
        when(repository.findAllByIntegrante(integrante)).thenReturn(relaciones);
        doNothing().when(repository).deleteAll(relaciones);

        service.eliminarRelacionesPosicion(integrante);

        verify(repository).findAllByIntegrante(integrante);
        verify(repository).deleteAll(relaciones);
    }
}
