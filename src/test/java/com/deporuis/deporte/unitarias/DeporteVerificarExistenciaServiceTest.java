package com.deporuis.deporte.unitarias;

import com.deporuis.deporte.aplicacion.DeporteQueryService;
import com.deporuis.deporte.aplicacion.helper.DeporteVerificarExistenciaService;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.excepciones.DeporteNotFoundException;
import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeporteVerificarExistenciaServiceTest {
    @Mock private DeporteRepository deporteRepository;
    @Mock private DeporteQueryService deporteQueryService;

    @InjectMocks
    private DeporteVerificarExistenciaService service;

    @BeforeEach
    void init() { MockitoAnnotations.openMocks(this); }

    @Test
    void verificarDeporte_cuandoNoExiste_debeLanzarNotFound() {
        when(deporteRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(DeporteNotFoundException.class, () -> service.verificarDeporte(999));
        verify(deporteRepository).findById(999);
    }

    @Test
    void verificarDeporte_siExiste_debeRetornarEntidad() {
        Deporte d = new Deporte();
        d.setIdDeporte(7);
        when(deporteRepository.findById(7)).thenReturn(Optional.of(d));
        Deporte out = service.verificarDeporte(7);
        assertEquals(7, out.getIdDeporte());
    }

    @Test
    void verificarDeporteNoExisteYReactivar_cuandoExisteInvisible_reactivaYActualizaDescripcion() {
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("Fútbol");
        req.setDescripcionDeporte("Desc nueva");

        Deporte existente = new Deporte();
        existente.setIdDeporte(1);
        existente.setNombreDeporte("Futbol");
        existente.setDescripcionDeporte("Antigua");
        existente.setVisibilidad(false);

        when(deporteQueryService.buscarPorNombreNormalizado("Fútbol")).thenReturn(Optional.of(existente));
        when(deporteRepository.save(any(Deporte.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Deporte> res = service.verificarDeporteNoExisteYReactivarSiAplica(req);
        assertTrue(res.isPresent());
        Deporte react = res.get();
        assertTrue(react.getVisibilidad(), "Debe quedar visible");
        assertEquals("Desc nueva", react.getDescripcionDeporte());
        verify(deporteRepository).save(any(Deporte.class));
    }

    @Test
    void verificarDeporteNoExisteYReactivar_cuandoExisteVisible_debeLanzarYaExiste() {
        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte("Fútbol");
        req.setDescripcionDeporte("Desc");

        Deporte existente = new Deporte();
        existente.setIdDeporte(2);
        existente.setNombreDeporte("Futbol");
        existente.setVisibilidad(true);

        when(deporteQueryService.buscarPorNombreNormalizado("Fútbol")).thenReturn(Optional.of(existente));

        assertThrows(DeporteYaExisteException.class, () -> service.verificarDeporteNoExisteYReactivarSiAplica(req));
    }

    @Test
    void validarNombreNoDuplicado_cuandoDup_debeLanzarExcepcion() {
        when(deporteQueryService.existeNombreNormalizadoExcluyendoId("futbol", 1)).thenReturn(true);
        assertThrows(DeporteYaExisteException.class, () -> service.validarNombreNoDuplicado("futbol", 1));
    }

    @Test
    void validarNombreNoDuplicado_cuandoNoDup_noLanza() {
        when(deporteQueryService.existeNombreNormalizadoExcluyendoId("tenis", 1)).thenReturn(false);
        assertDoesNotThrow(() -> service.validarNombreNoDuplicado("tenis", 1));
    }
}
