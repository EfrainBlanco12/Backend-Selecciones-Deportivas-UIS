package com.deporuis.seleccion.unitarias;

import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.aplicacion.helper.SeleccionRelacionService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.infraestructura.SeleccionHorarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeleccionRelacionServiceTest {

    @InjectMocks private SeleccionRelacionService service;
    @Mock private SeleccionHorarioRepository seleccionHorarioRepository;

    @Test
    void crearRelacionesHorarios_persisteRelacionesConSeleccionYHorarios() {
        Seleccion seleccion = mock(Seleccion.class);
        Horario h1 = mock(Horario.class);
        Horario h2 = mock(Horario.class);

        service.crearRelacionesHorarios(seleccion, List.of(h1, h2));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<SeleccionHorario>> captor = ArgumentCaptor.forClass(List.class);
        verify(seleccionHorarioRepository).saveAll(captor.capture());
        List<SeleccionHorario> guardados = captor.getValue();

        assertNotNull(guardados);
        assertEquals(2, guardados.size());

        SeleccionHorario r1 = guardados.get(0);
        SeleccionHorario r2 = guardados.get(1);

        assertSame(seleccion, r1.getSeleccion());
        assertSame(seleccion, r2.getSeleccion());

        assertTrue(List.of(h1, h2).contains(r1.getHorario()));
        assertTrue(List.of(h1, h2).contains(r2.getHorario()));

        verifyNoMoreInteractions(seleccionHorarioRepository);
    }

    @Test
    void eliminarRelacionesSeleccion_borraTodo() {
        Seleccion seleccion = mock(Seleccion.class);
        List<SeleccionHorario> existentes = List.of(new SeleccionHorario(), new SeleccionHorario());
        when(seleccionHorarioRepository.findAllBySeleccion(seleccion)).thenReturn(existentes);

        service.eliminarRelacionesSeleccion(seleccion);

        verify(seleccionHorarioRepository).findAllBySeleccion(seleccion);
        verify(seleccionHorarioRepository).deleteAll(existentes);
        verifyNoMoreInteractions(seleccionHorarioRepository);
    }
}
