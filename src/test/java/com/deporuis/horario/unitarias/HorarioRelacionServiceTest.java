package com.deporuis.horario.unitarias;

import com.deporuis.horario.aplicacion.helper.HorarioRelacionService;
import com.deporuis.horario.dominio.DiaHorario;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.infraestructura.SeleccionHorarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioRelacionServiceTest {

    @Mock private SeleccionHorarioRepository seleccionHorarioRepository;
    @Mock private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;
    @InjectMocks private HorarioRelacionService service;

    @Captor private ArgumentCaptor<List<SeleccionHorario>> captor;

    @Test
    void crearRelacionesConSelecciones_conVariasSelecciones_creaTodasLasRelaciones() {
        Horario horario = new Horario(DiaHorario.LUNES, LocalTime.of(8, 0), LocalTime.of(10, 0));
        List<Integer> idSelecciones = List.of(1, 2, 3);

        Seleccion sel1 = mock(Seleccion.class);
        Seleccion sel2 = mock(Seleccion.class);
        Seleccion sel3 = mock(Seleccion.class);

        when(seleccionVerificarExistenciaService.verificarSeleccion(1)).thenReturn(sel1);
        when(seleccionVerificarExistenciaService.verificarSeleccion(2)).thenReturn(sel2);
        when(seleccionVerificarExistenciaService.verificarSeleccion(3)).thenReturn(sel3);

        List<SeleccionHorario> relacionesGuardadas = List.of(
                new SeleccionHorario(),
                new SeleccionHorario(),
                new SeleccionHorario()
        );
        when(seleccionHorarioRepository.saveAll(anyList())).thenReturn(relacionesGuardadas);

        List<SeleccionHorario> result = service.crearRelacionesConSelecciones(horario, idSelecciones);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(seleccionVerificarExistenciaService).verificarSeleccion(1);
        verify(seleccionVerificarExistenciaService).verificarSeleccion(2);
        verify(seleccionVerificarExistenciaService).verificarSeleccion(3);
        verify(seleccionHorarioRepository).saveAll(captor.capture());

        List<SeleccionHorario> relacionesCreadas = captor.getValue();
        assertEquals(3, relacionesCreadas.size());
    }

    @Test
    void crearRelacionesConSelecciones_conUnaSeleccion_creaUnaRelacion() {
        Horario horario = new Horario(DiaHorario.MARTES, LocalTime.of(14, 0), LocalTime.of(16, 0));
        List<Integer> idSelecciones = List.of(5);

        Seleccion seleccion = mock(Seleccion.class);
        when(seleccionVerificarExistenciaService.verificarSeleccion(5)).thenReturn(seleccion);

        List<SeleccionHorario> relacionesGuardadas = List.of(new SeleccionHorario());
        when(seleccionHorarioRepository.saveAll(anyList())).thenReturn(relacionesGuardadas);

        List<SeleccionHorario> result = service.crearRelacionesConSelecciones(horario, idSelecciones);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(seleccionVerificarExistenciaService).verificarSeleccion(5);
        verify(seleccionHorarioRepository).saveAll(anyList());
    }

    @Test
    void crearRelacionesConSelecciones_conListaVacia_retornaListaVacia() {
        Horario horario = new Horario(DiaHorario.MIERCOLES, LocalTime.of(10, 0), LocalTime.of(12, 0));
        List<Integer> idSelecciones = List.of();

        List<SeleccionHorario> result = service.crearRelacionesConSelecciones(horario, idSelecciones);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(seleccionVerificarExistenciaService, never()).verificarSeleccion(any());
        verify(seleccionHorarioRepository, never()).saveAll(anyList());
    }

    @Test
    void crearRelacionesConSelecciones_conNull_retornaListaVacia() {
        Horario horario = new Horario(DiaHorario.JUEVES, LocalTime.of(16, 0), LocalTime.of(18, 0));

        List<SeleccionHorario> result = service.crearRelacionesConSelecciones(horario, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(seleccionVerificarExistenciaService, never()).verificarSeleccion(any());
        verify(seleccionHorarioRepository, never()).saveAll(anyList());
    }
}
