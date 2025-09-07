package com.deporuis.publicacion.unitarias;

import com.deporuis.publicacion.aplicacion.PublicacionQueryService;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicacionQueryServiceTest {

    private PublicacionRepository repo;
    private PublicacionQueryService service;

    @BeforeEach
    void setUp() {
        repo = mock(PublicacionRepository.class);
        service = new PublicacionQueryService(repo);
    }

    @Test
    void obtenerPublicacion_deberiaUsarFindByIdPublicacionAndVisibilidadTrueYArmarIdSelecciones() {
        var pub = new Publicacion();
        pub.setIdPublicacion(7);
        pub.setTitulo("Inicio T");
        pub.setTipoPublicacion(TipoPublicacion.NOTICIA);
        pub.setFecha(LocalDateTime.of(2024,1,1,9,0));

        when(repo.findByIdPublicacionAndVisibilidadTrue(7)).thenReturn(Optional.of(pub));
        when(repo.findSeleccionIdsByPublicacionId(7)).thenReturn(List.of(1, 2, 3));

        PublicacionResponse dto = service.obtenerPublicacion(7);

        assertEquals(7, dto.getIdPublicacion());
        assertEquals(List.of(1,2,3), dto.getIdSelecciones());
        assertEquals("NOTICIA", dto.getTipoPublicacion());
        verify(repo).findByIdPublicacionAndVisibilidadTrue(7);
        verify(repo).findSeleccionIdsByPublicacionId(7);
    }

    @Test
    void obtenerPublicacion_deberiaLanzarNotFoundSiNoExisteVisibile() {
        when(repo.findByIdPublicacionAndVisibilidadTrue(99)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.obtenerPublicacion(99));
    }

    @Test
    void obtenerPublicacionesPaginadas_deberiaMapearContenidoYUsarFindSeleccionIdsPorCadaElemento() {
        var p1 = new Publicacion(); p1.setIdPublicacion(1); p1.setTipoPublicacion(TipoPublicacion.EVENTO);
        var p2 = new Publicacion(); p2.setIdPublicacion(2); p2.setTipoPublicacion(TipoPublicacion.NOTICIA);

        Page<Publicacion> page = new PageImpl<>(List.of(p1, p2), PageRequest.of(0, 5, Sort.by("fecha").descending()), 2);

        when(repo.findByVisibilidadTrue(ArgumentMatchers.any(Pageable.class))).thenReturn(page);
        when(repo.findSeleccionIdsByPublicacionId(1)).thenReturn(List.of(10));
        when(repo.findSeleccionIdsByPublicacionId(2)).thenReturn(List.of(20, 30));

        Page<PublicacionResponse> result = service.obtenerPublicacionesPaginadas(0, 5);

        assertEquals(2, result.getTotalElements());
        assertEquals(List.of(10), result.getContent().get(0).getIdSelecciones());
        assertEquals(List.of(20,30), result.getContent().get(1).getIdSelecciones());
    }

    @Test
    void obtenerPublicacionesPorTipoPaginadas_deberiaUsarFindByVisibilidadTrueAndTipoPublicacion() {
        var p = new Publicacion(); p.setIdPublicacion(5); p.setTipoPublicacion(TipoPublicacion.NOTICIA);
        Page<Publicacion> page = new PageImpl<>(List.of(p), PageRequest.of(0, 5), 1);

        when(repo.findByVisibilidadTrueAndTipoPublicacion(eq(TipoPublicacion.NOTICIA), any(Pageable.class))).thenReturn(page);
        when(repo.findSeleccionIdsByPublicacionId(5)).thenReturn(List.of(1));

        Page<PublicacionResponse> result = service.obtenerPublicacionesPorTipoPaginadas(TipoPublicacion.NOTICIA, 0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals(List.of(1), result.getContent().get(0).getIdSelecciones());
        verify(repo).findByVisibilidadTrueAndTipoPublicacion(eq(TipoPublicacion.NOTICIA), any(Pageable.class));
    }
}
