package com.deporuis.seleccion.unitarias;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.seleccion.aplicacion.SeleccionCommandService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionRelacionService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeleccionCommandServiceTest {

    @InjectMocks private SeleccionCommandService service;
    @Mock private SeleccionRepository seleccionRepository;
    @Mock private FotoCommandService fotoCommandService;
    @Mock private SeleccionRelacionService seleccionRelacionService;
    @Mock private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    private Seleccion seleccion;

    @BeforeEach
    void setup() {
        seleccion = mock(Seleccion.class);
    }

    @Test
    void eliminarSeleccion_flujoCompleto() {
        when(seleccionVerificarExistenciaService.verificarSeleccion(10)).thenReturn(seleccion);
        doNothing().when(seleccionRelacionService).eliminarRelacionesSeleccion(seleccion);
        doNothing().when(fotoCommandService).eliminarFotosSeleccion(seleccion);
        doNothing().when(seleccionRepository).delete(seleccion);

        service.eliminarSeleccion(10);

        verify(seleccionVerificarExistenciaService).verificarSeleccion(10);
        verify(seleccionRelacionService).eliminarRelacionesSeleccion(seleccion);
        verify(fotoCommandService).eliminarFotosSeleccion(seleccion);
        verify(seleccionRepository).delete(seleccion);
        verifyNoMoreInteractions(seleccionRepository, fotoCommandService, seleccionRelacionService, seleccionVerificarExistenciaService);
    }

    @Test
    void softDeleteSeleccion_marcarInvisibleYGuardar() {
        when(seleccionVerificarExistenciaService.verificarSeleccion(33)).thenReturn(seleccion);

        service.softDeleteSeleccion(33);

        verify(seleccionVerificarExistenciaService).verificarSeleccion(33);
        verify(seleccion).setVisibilidad(false);
        verify(seleccionRepository).save(seleccion);
        verifyNoMoreInteractions(seleccionRepository, fotoCommandService, seleccionRelacionService, seleccionVerificarExistenciaService);
    }
}
