package com.deporuis.publicacion.unitarias;

import com.deporuis.publicacion.aplicacion.PublicacionCommandService;
import com.deporuis.publicacion.aplicacion.PublicacionQueryService;
import com.deporuis.publicacion.aplicacion.PublicacionService;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la fachada PublicacionService.
 * Verifica que delega correctamente en CommandService y QueryService.
 */
@ExtendWith(MockitoExtension.class)
class PublicacionServiceTest {

    @Mock private PublicacionCommandService commandService;
    @Mock private PublicacionQueryService queryService;

    @InjectMocks
    private PublicacionService service; // fachada real

    private PublicacionRequest req;
    private PublicacionResponse resp;

    @BeforeEach
    void setUp() {
        req = new PublicacionRequest();
        req.setTitulo("Inicio");
        req.setDescripcion("Desc");
        req.setLugar("Cancha A");
        req.setFecha(LocalDateTime.of(2024,1,1,9,0));
        req.setDuracion("2h");
        req.setTipoPublicacion(TipoPublicacion.NOTICIA);

        resp = new PublicacionResponse();
        resp.setIdPublicacion(1);
        resp.setTitulo("Inicio");
        resp.setDescripcion("Desc");
        resp.setLugar("Cancha A");
        resp.setFecha(LocalDateTime.of(2024,1,1,9,0));
        resp.setDuracion("2h");
        resp.setVisibilidad(true);
        resp.setTipoPublicacion(TipoPublicacion.NOTICIA.name());
        resp.setFechaCreacion(LocalDateTime.of(2024,1,1,9,0));
        
        var sel1 = new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(10, "Selección 1");
        var sel2 = new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(20, "Selección 2");
        resp.setIdSelecciones(List.of(sel1, sel2));
    }

    @Test
    void crearPublicacion_delegaEnCommandService_yRetornaResponse() {
        when(commandService.crearPublicacion(any(PublicacionRequest.class), eq(100))).thenReturn(resp);

        PublicacionResponse out = service.crearPublicacion(req, 100);

        assertNotNull(out);
        assertEquals(1, out.getIdPublicacion());
        verify(commandService).crearPublicacion(eq(req), eq(100));
        verifyNoInteractions(queryService);
    }

    @Test
    void actualizarPublicacion_delegaEnCommandService_yRetornaResponse() {
        when(commandService.actualizarPublicacion(eq(7), any(PublicacionRequest.class), eq(200))).thenReturn(resp);

        PublicacionResponse out = service.actualizarPublicacion(7, req, 200);

        assertNotNull(out);
        assertEquals(1, out.getIdPublicacion());
        verify(commandService).actualizarPublicacion(eq(7), eq(req), eq(200));
        verifyNoInteractions(queryService);
    }

    @Test
    void obtenerPublicacion_delegaEnQueryService_yRetornaResponse() {
        when(queryService.obtenerPublicacion(5)).thenReturn(resp);

        PublicacionResponse out = service.obtenerPublicacion(5);

        assertNotNull(out);
        assertEquals(1, out.getIdPublicacion());
        verify(queryService).obtenerPublicacion(5);
        verifyNoInteractions(commandService);
    }

    @Test
    void obtenerPublicacionesPaginadas_delegaEnQueryService() {
        PageImpl<PublicacionResponse> page =
                new PageImpl<>(List.of(resp), PageRequest.of(0,5, Sort.by("fecha").descending()), 1);

        when(queryService.obtenerPublicacionesPaginadas(0,5)).thenReturn(page);

        Page<PublicacionResponse> out = service.obtenerPublicacionesPaginadas(0,5);

        assertEquals(1, out.getTotalElements());
        assertEquals(1, out.getContent().get(0).getIdPublicacion());
        verify(queryService).obtenerPublicacionesPaginadas(0,5);
        verifyNoInteractions(commandService);
    }

    @Test
    void obtenerNoticiasPaginadas_delegaEnQueryService() {
        PageImpl<PublicacionResponse> page =
                new PageImpl<>(List.of(resp), PageRequest.of(0,5), 1);

        when(queryService.obtenerPublicacionesPorTipoPaginadas(eq(TipoPublicacion.NOTICIA), eq(0), eq(5)))
                .thenReturn(page);

        Page<PublicacionResponse> out = service.obtenerNoticiasPaginadas(0,5);

        assertEquals(1, out.getTotalElements());
        verify(queryService).obtenerPublicacionesPorTipoPaginadas(eq(TipoPublicacion.NOTICIA), eq(0), eq(5));
        verifyNoInteractions(commandService);
    }

    @Test
    void obtenerEventosPaginados_delegaEnQueryService() {
        PageImpl<PublicacionResponse> page =
                new PageImpl<>(List.of(resp), PageRequest.of(1, 10), 11);

        when(queryService.obtenerPublicacionesPorTipoPaginadas(eq(TipoPublicacion.EVENTO), eq(1), eq(10)))
                .thenReturn(page);

        Page<PublicacionResponse> out = service.obtenerEventosPaginados(1, 10);

        assertEquals(1, out.getContent().size());
        assertEquals(11, out.getTotalElements());

        verify(queryService).obtenerPublicacionesPorTipoPaginadas(eq(TipoPublicacion.EVENTO), eq(1), eq(10));
        verifyNoInteractions(commandService);
    }


    @Test
    void softDeletePublicacion_delegaEnCommandService() {
        service.softDeletePublicacion(12);
        verify(commandService).softDeletePublicacion(12);
        verifyNoInteractions(queryService);
    }

    @Test
    void eliminarPublicacion_delegaEnCommandService() {
        service.eliminarPublicacion(33);
        verify(commandService).eliminarPublicacion(33);
        verifyNoInteractions(queryService);
    }
}
