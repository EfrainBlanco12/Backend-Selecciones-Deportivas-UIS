package com.deporuis.deporte.unitarias;

import com.deporuis.deporte.aplicacion.DeporteCommandService;
import com.deporuis.deporte.aplicacion.DeporteQueryService;
import com.deporuis.deporte.aplicacion.helper.DeporteVerificarExistenciaService;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteNotFoundException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.deporuis.deporte.infraestructura.dto.DeporteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeporteCommandServiceTest {
    @Mock private DeporteRepository deporteRepository;
    @Mock private DeporteQueryService deporteQueryService;
    @Mock private DeporteVerificarExistenciaService deporteVerificarExistenciaService;

    @InjectMocks
    private DeporteCommandService commandService;

    @BeforeEach void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void crearDeporte_siReactivado_debeRetornarResponseDelExistente() {
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("Fútbol");
        req.setDescripcionDeporte("Desc");

        Deporte inactivo = new Deporte();
        inactivo.setIdDeporte(5);
        inactivo.setNombreDeporte("Fútbol");
        inactivo.setVisibilidad(true);

        when(deporteVerificarExistenciaService.verificarDeporteNoExisteYReactivarSiAplica(req))
                .thenReturn(Optional.of(inactivo));

        DeporteResponse out = commandService.crearDeporte(req);
        assertEquals(5, out.getIdDeporte());
        assertEquals("Fútbol", out.getNombreDeporte());
        verify(deporteRepository, never()).save(any());
    }

    @Test
    void crearDeporte_siNoExiste_creaNuevo() {
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("Tenis");
        req.setDescripcionDeporte("Desc");

        when(deporteVerificarExistenciaService.verificarDeporteNoExisteYReactivarSiAplica(req))
                .thenReturn(Optional.empty());

        when(deporteRepository.save(any(Deporte.class))).thenAnswer(inv -> {
            Deporte d = inv.getArgument(0);
            d.setIdDeporte(10);
            return d;
        });

        DeporteResponse out = commandService.crearDeporte(req);
        assertEquals(10, out.getIdDeporte());
        assertEquals("Tenis", out.getNombreDeporte());
        verify(deporteRepository).save(any(Deporte.class));
    }

    @Test
    void actualizarDeporte_ok_actualizaNombreYDescripcion() {
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("Nuevo");
        req.setDescripcionDeporte("Desc nueva");

        Deporte existente = new Deporte();
        existente.setIdDeporte(3);
        existente.setNombreDeporte("Viejo");
        existente.setDescripcionDeporte("Desc vieja");
        existente.setVisibilidad(true);

        when(deporteQueryService.buscarPorId(3)).thenReturn(Optional.of(existente));
        when(deporteRepository.save(any(Deporte.class))).thenAnswer(inv -> inv.getArgument(0));

        DeporteResponse resp = commandService.actualizarDeporte(3, req);

        assertEquals(3, resp.getIdDeporte());
        assertEquals("Nuevo", resp.getNombreDeporte());
        // validarNombreNoDuplicado es invocado dentro
        verify(deporteVerificarExistenciaService).validarNombreNoDuplicado("Nuevo", 3);
    }

    @Test
    void actualizarDeporte_noExiste_deberiaLanzarNotFound() {
        when(deporteQueryService.buscarPorId(99)).thenReturn(Optional.empty());
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("X");
        req.setDescripcionDeporte("Y");
        assertThrows(DeporteNotFoundException.class, () -> commandService.actualizarDeporte(99, req));
    }

    @Test
    void softDeleteDeporte_ok_cambiaVisibilidadAFalso() {
        Deporte existente = new Deporte();
        existente.setIdDeporte(4);
        existente.setNombreDeporte("Boxeo");
        existente.setVisibilidad(true);

        when(deporteQueryService.buscarPorId(4)).thenReturn(Optional.of(existente));
        when(deporteRepository.save(any(Deporte.class))).thenAnswer(inv -> inv.getArgument(0));

        DeporteResponse resp = commandService.softDeleteDeporte(4);
        assertEquals(4, resp.getIdDeporte());
        // Verificar que el objeto guardado haya quedado invisible
        verify(deporteRepository).save(argThat(d -> d.getVisibilidad() == Boolean.FALSE));
    }
}
