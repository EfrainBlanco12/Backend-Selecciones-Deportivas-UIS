package com.deporuis.publicacion.unitarias;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.publicacion.aplicacion.PublicacionCommandService;
import com.deporuis.publicacion.aplicacion.helper.PublicacionRelacionService;
import com.deporuis.publicacion.aplicacion.helper.PublicacionVerificarExistenciaService;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicacionCommandServiceTest {

    @InjectMocks private PublicacionCommandService service;
    @Mock private PublicacionRepository publicacionRepository;
    @Mock private PublicacionVerificarExistenciaService verificarExistenciaService;
    @Mock private PublicacionRelacionService relacionService;
    @Mock private FotoCommandService fotoCommandService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    private PublicacionRequest requestSample() {
        var fr = new FotoRequest();
        fr.setContenido(new byte[]{1,2,3});
        fr.setTemporada(2024);

        var req = new PublicacionRequest();
        req.setTitulo("Inicio");
        req.setDescripcion("Desc");
        req.setLugar("Cancha A");
        req.setFecha(LocalDateTime.of(2024,1,1,9,0));
        req.setDuracion("2h");
        req.setTipoPublicacion(TipoPublicacion.NOTICIA);
        req.setSelecciones(List.of(1,2));
        req.setFotos(List.of(fr));
        return req;
    }

    @Test
    void crearPublicacion_deberiaGuardarEntidad_CrearRelacionesYFotos_YRetornarResponse() {
        var req = requestSample();

        // al guardar debe quedar con id
        when(publicacionRepository.save(any(Publicacion.class))).thenAnswer(inv -> {
            Publicacion p = inv.getArgument(0);
            p.setIdPublicacion(99);
            return p;
        });

        // selecciones verificadas
        var s1 = new Seleccion(); s1.setIdSeleccion(1);
        var s2 = new Seleccion(); s2.setIdSeleccion(2);
        when(verificarExistenciaService.verificarSelecciones(List.of(1,2))).thenReturn(List.of(s1, s2));

        // relaciones creadas
        when(relacionService.crearRelacionesSeleccion(any(Publicacion.class), eq(List.of(s1, s2))))
                .thenAnswer(inv -> {
                    Publicacion p = inv.getArgument(0);
                    return List.of(new SeleccionPublicacion(null, s1, p),
                            new SeleccionPublicacion(null, s2, p));
                });

        // fotos creadas/verificadas
        var foto = new Foto(new byte[]{1,2,3}, 2024);
        when(fotoCommandService.crearFotosPublicacion(eq(req.getFotos()), any(Publicacion.class)))
                .thenReturn(List.of(foto));
        when(verificarExistenciaService.verificarFotos(List.of(foto))).thenReturn(List.of(foto));

        PublicacionResponse resp = service.crearPublicacion(req, 100);

        assertNotNull(resp);
        assertEquals(99, resp.getIdPublicacion());
        assertEquals(List.of(1,2), resp.getIdSelecciones());
        
        // Verificar que se establecieron campos de auditoría
        ArgumentCaptor<Publicacion> captor = ArgumentCaptor.forClass(Publicacion.class);
        verify(publicacionRepository).save(captor.capture());
        assertEquals(100, captor.getValue().getUsuarioModifico());
        assertNotNull(captor.getValue().getFechaModificacion());
        verify(relacionService).crearRelacionesSeleccion(any(Publicacion.class), anyList());
        verify(fotoCommandService).crearFotosPublicacion(eq(req.getFotos()), any(Publicacion.class));
    }

    @Test
    void actualizarPublicacion_deberiaActualizarCampos_YActualizarRelaciones_YGuardar() {
        var req = requestSample();
        var existente = new Publicacion();
        existente.setIdPublicacion(50);

        when(verificarExistenciaService.verificarPublicacion(50)).thenReturn(existente);

        var s1 = new Seleccion(); s1.setIdSeleccion(1);
        var s2 = new Seleccion(); s2.setIdSeleccion(2);
        when(verificarExistenciaService.verificarSelecciones(List.of(1,2))).thenReturn(List.of(s1, s2));

        when(relacionService.actualizarRelacionesSeleccion(eq(existente), eq(List.of(s1, s2)), eq(List.of(1,2))))
                .thenReturn(List.of(new SeleccionPublicacion(null, s1, existente),
                        new SeleccionPublicacion(null, s2, existente)));

        when(publicacionRepository.save(existente)).thenReturn(existente);

        PublicacionResponse resp = service.actualizarPublicacion(50, req, 200);

        assertNotNull(resp);
        assertEquals(200, existente.getUsuarioModifico());
        assertNotNull(existente.getFechaModificacion());
        verify(publicacionRepository).save(existente);
        verify(relacionService).actualizarRelacionesSeleccion(eq(existente), eq(List.of(s1, s2)), eq(List.of(1,2)));
    }

    @Test
    void eliminarPublicacion_deberiaEliminarRelaciones_Fotos_YEntidad() {
        var existente = new Publicacion();
        existente.setIdPublicacion(10);
        when(verificarExistenciaService.verificarPublicacion(10)).thenReturn(existente);

        service.eliminarPublicacion(10);

        verify(relacionService).eliminarRelacionesSeleccion(existente);
        verify(fotoCommandService).eliminarFotosPublicacion(existente);
        verify(publicacionRepository).delete(existente);
    }

    @Test
    void softDeletePublicacion_deberiaMarcarVisibilidadFalseYGuardar() {
        var existente = new Publicacion();
        existente.setIdPublicacion(10);
        existente.setVisibilidad(true);

        when(verificarExistenciaService.verificarPublicacion(10)).thenReturn(existente);

        service.softDeletePublicacion(10);

        ArgumentCaptor<Publicacion> captor = ArgumentCaptor.forClass(Publicacion.class);
        verify(publicacionRepository).save(captor.capture());
        assertFalse(captor.getValue().getVisibilidad());
    }
}
