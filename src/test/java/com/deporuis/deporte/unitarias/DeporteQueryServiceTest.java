package com.deporuis.deporte.unitarias;

import com.deporuis.deporte.aplicacion.DeporteQueryService;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeporteQueryServiceTest {
    @Mock
    private DeporteRepository deporteRepository;

    @InjectMocks
    private DeporteQueryService queryService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerTodosLosDeportesVisibles_deberiaMapearAResponse() {
        Deporte d1 = new Deporte();
        d1.setIdDeporte(1);
        d1.setNombreDeporte("Fútbol");
        d1.setDescripcionDeporte("Desc 1");
        d1.setVisibilidad(true);

        Deporte d2 = new Deporte();
        d2.setIdDeporte(2);
        d2.setNombreDeporte("Baloncesto");
        d2.setDescripcionDeporte("Desc 2");
        d2.setVisibilidad(true);

        when(deporteRepository.findAllByVisibilidadTrue()).thenReturn(Arrays.asList(d1, d2));

        List<DeporteResponse> resp = queryService.obtenerTodosLosDeportesVisibles();

        assertEquals(2, resp.size());
        assertEquals(1, resp.get(0).getIdDeporte());
        assertEquals("Fútbol", resp.get(0).getNombreDeporte());
        assertEquals(2, resp.get(1).getIdDeporte());
        verify(deporteRepository).findAllByVisibilidadTrue();
    }

    @Test
    void buscarPorId_deberiaUsarRepoYRetornarOptional() {
        Deporte d1 = new Deporte();
        d1.setIdDeporte(10);
        d1.setNombreDeporte("Vóleibol");
        d1.setVisibilidad(true);

        when(deporteRepository.findByIdDeporteAndVisibilidadTrue(10)).thenReturn(Optional.of(d1));

        Optional<Deporte> res = queryService.buscarPorId(10);
        assertTrue(res.isPresent());
        assertEquals(10, res.get().getIdDeporte());
        verify(deporteRepository).findByIdDeporteAndVisibilidadTrue(10);
    }

    @Test
    void existeNombreNormalizadoExcluyendoId_deberiaDetectarDuplicadoIgnorandoAcentos() {
        Deporte d1 = new Deporte(); d1.setIdDeporte(1); d1.setNombreDeporte("Fútbol");
        Deporte d2 = new Deporte(); d2.setIdDeporte(2); d2.setNombreDeporte("Futbol");

        when(deporteRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        boolean dup = queryService.existeNombreNormalizadoExcluyendoId("futbol", 1);
        assertTrue(dup, "Debe detectar duplicado para 'futbol' ignorando acentos y excluyendo id 1");
        verify(deporteRepository).findAll();
    }
}
