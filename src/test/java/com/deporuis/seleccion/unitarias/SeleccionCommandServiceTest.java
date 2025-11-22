package com.deporuis.seleccion.unitarias;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.horario.aplicacion.HorarioCommandService;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.aplicacion.SeleccionCommandService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionRelacionService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.dominio.TipoSeleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeleccionCommandServiceTest {

    @InjectMocks private SeleccionCommandService service;
    @Mock private SeleccionRepository seleccionRepository;
    @Mock private FotoCommandService fotoCommandService;
    @Mock private HorarioCommandService horarioCommandService;
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

    @Test
    void actualizarSeleccionParcial_soloNombre() {
        SeleccionPatchRequest patchRequest = new SeleccionPatchRequest();
        patchRequest.setNombreSeleccion("Nuevo Nombre");

        when(seleccionVerificarExistenciaService.verificarSeleccion(1)).thenReturn(seleccion);
        when(seleccionRepository.save(seleccion)).thenReturn(seleccion);

        service.actualizarSeleccionParcial(1, patchRequest);

        verify(seleccionVerificarExistenciaService).verificarSeleccion(1);
        verify(seleccion).setNombreSeleccion("Nuevo Nombre");
        verify(seleccion, never()).setFechaCreacion(any());
        verify(seleccion, never()).setEspacioDeportivo(any());
        verify(seleccionRepository).save(seleccion);
    }

    @Test
    void actualizarSeleccionParcial_soloDeporte() {
        SeleccionPatchRequest patchRequest = new SeleccionPatchRequest();
        patchRequest.setIdDeporte(5);

        Deporte deporte = mock(Deporte.class);
        when(seleccionVerificarExistenciaService.verificarSeleccion(2)).thenReturn(seleccion);
        when(seleccionVerificarExistenciaService.verificarDeporte(5)).thenReturn(deporte);
        when(seleccionRepository.save(seleccion)).thenReturn(seleccion);

        service.actualizarSeleccionParcial(2, patchRequest);

        verify(seleccionVerificarExistenciaService).verificarSeleccion(2);
        verify(seleccionVerificarExistenciaService).verificarDeporte(5);
        verify(seleccion).setDeporte(deporte);
        verify(seleccionRepository).save(seleccion);
    }

    @Test
    void actualizarSeleccionParcial_tipoSeleccionYEquipo() {
        SeleccionPatchRequest patchRequest = new SeleccionPatchRequest();
        patchRequest.setTipo_seleccion(TipoSeleccion.FEMENINO);
        patchRequest.setEquipo(true);

        when(seleccionVerificarExistenciaService.verificarSeleccion(3)).thenReturn(seleccion);
        when(seleccionRepository.save(seleccion)).thenReturn(seleccion);

        service.actualizarSeleccionParcial(3, patchRequest);

        verify(seleccionVerificarExistenciaService).verificarSeleccion(3);
        verify(seleccion).setTipo_seleccion(TipoSeleccion.FEMENINO);
        verify(seleccion).setEquipo(true);
        verify(seleccionRepository).save(seleccion);
    }

    @Test
    void actualizarSeleccionParcial_conFotos() {
        SeleccionPatchRequest patchRequest = new SeleccionPatchRequest();
        patchRequest.setFotos(Collections.emptyList());

        List<Foto> nuevasFotos = List.of(mock(Foto.class));
        when(seleccionVerificarExistenciaService.verificarSeleccion(4)).thenReturn(seleccion);
        when(fotoCommandService.crearFotosSeleccion(anyList(), eq(seleccion))).thenReturn(nuevasFotos);
        when(seleccionVerificarExistenciaService.verificarFotos(nuevasFotos)).thenReturn(nuevasFotos);
        when(seleccionRepository.save(seleccion)).thenReturn(seleccion);

        service.actualizarSeleccionParcial(4, patchRequest);

        verify(seleccionVerificarExistenciaService).verificarSeleccion(4);
        verify(fotoCommandService).eliminarFotosSeleccion(seleccion);
        verify(fotoCommandService).crearFotosSeleccion(anyList(), eq(seleccion));
        verify(seleccionVerificarExistenciaService).verificarFotos(nuevasFotos);
        verify(seleccion).setFotos(nuevasFotos);
        verify(seleccionRepository).save(seleccion);
    }

    @Test
    void actualizarSeleccionParcial_conHorarios() {
        SeleccionPatchRequest patchRequest = new SeleccionPatchRequest();
        patchRequest.setHorarios(Collections.emptyList());

        List<Horario> nuevosHorarios = List.of(mock(Horario.class));
        List<SeleccionHorario> relaciones = List.of(mock(SeleccionHorario.class));
        
        when(seleccionVerificarExistenciaService.verificarSeleccion(5)).thenReturn(seleccion);
        when(horarioCommandService.obtenerOcrearHorariosSeleccion(anyList())).thenReturn(nuevosHorarios);
        when(seleccionVerificarExistenciaService.verificarHorarios(nuevosHorarios)).thenReturn(nuevosHorarios);
        when(seleccionRelacionService.crearRelacionesHorarios(seleccion, nuevosHorarios)).thenReturn(relaciones);
        when(seleccionRepository.save(seleccion)).thenReturn(seleccion);

        service.actualizarSeleccionParcial(5, patchRequest);

        verify(seleccionVerificarExistenciaService).verificarSeleccion(5);
        verify(seleccionRelacionService).eliminarRelacionesSeleccion(seleccion);
        verify(horarioCommandService).obtenerOcrearHorariosSeleccion(anyList());
        verify(seleccionVerificarExistenciaService).verificarHorarios(nuevosHorarios);
        verify(seleccionRelacionService).crearRelacionesHorarios(seleccion, nuevosHorarios);
        verify(seleccion).setHorarios(relaciones);
        verify(seleccionRepository).save(seleccion);
    }
}
