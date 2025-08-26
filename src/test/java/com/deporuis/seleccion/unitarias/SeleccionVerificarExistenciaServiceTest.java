package com.deporuis.seleccion.unitarias;

import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.excepciones.SeleccionNotFoundException;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeleccionVerificarExistenciaServiceTest {

    @InjectMocks private SeleccionVerificarExistenciaService service;
    @Mock private SeleccionRepository repository;

    @Test
    void verificarSeleccion_noExiste_lanzaNotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        assertThrows(SeleccionNotFoundException.class, () -> service.verificarSeleccion(1));
        verify(repository).findById(1);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void verificarSeleccion_invisible_lanzaNotFound() {
        Seleccion seleccion = mock(Seleccion.class);
        when(repository.findById(2)).thenReturn(Optional.of(seleccion));
        when(seleccion.getVisibilidad()).thenReturn(Boolean.FALSE);
        assertThrows(SeleccionNotFoundException.class, () -> service.verificarSeleccion(2));
        verify(repository).findById(2);
        verify(seleccion).getVisibilidad();
        verifyNoMoreInteractions(repository, seleccion);
    }

    @Test
    void verificarSeleccion_visible_retorna() {
        Seleccion seleccion = mock(Seleccion.class);
        when(repository.findById(3)).thenReturn(Optional.of(seleccion));
        when(seleccion.getVisibilidad()).thenReturn(Boolean.TRUE);
        assertSame(seleccion, service.verificarSeleccion(3));
        verify(repository).findById(3);
        verify(seleccion).getVisibilidad();
        verifyNoMoreInteractions(repository, seleccion);
    }
}
