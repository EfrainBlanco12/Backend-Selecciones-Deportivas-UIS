package com.deporuis.publicacion.unitarias;

import com.deporuis.publicacion.aplicacion.PublicacionQueryService;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
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
        
        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1); s1.setNombreSeleccion("Sel1");
        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(2); s2.setNombreSeleccion("Sel2");
        Seleccion s3 = new Seleccion(); s3.setIdSeleccion(3); s3.setNombreSeleccion("Sel3");
        SeleccionPublicacion sp1 = new SeleccionPublicacion(null, s1, pub);
        SeleccionPublicacion sp2 = new SeleccionPublicacion(null, s2, pub);
        SeleccionPublicacion sp3 = new SeleccionPublicacion(null, s3, pub);
        pub.setSelecciones(List.of(sp1, sp2, sp3));

        when(repo.findByIdPublicacionAndVisibilidadTrue(7)).thenReturn(Optional.of(pub));
        when(repo.findSeleccionDtosByPublicacionId(7)).thenReturn(List.of(
            new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(1, "Sel1"),
            new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(2, "Sel2"),
            new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(3, "Sel3")
        ));

        PublicacionResponse dto = service.obtenerPublicacion(7);

        assertEquals(7, dto.getIdPublicacion());
        assertEquals(3, dto.getIdSelecciones().size());
        assertEquals(1, dto.getIdSelecciones().get(0).getIdSeleccion());
        assertEquals(2, dto.getIdSelecciones().get(1).getIdSeleccion());
        assertEquals(3, dto.getIdSelecciones().get(2).getIdSeleccion());
        assertEquals("NOTICIA", dto.getTipoPublicacion());
        verify(repo).findByIdPublicacionAndVisibilidadTrue(7);
    }

    @Test
    void obtenerPublicacion_deberiaLanzarNotFoundSiNoExisteVisibile() {
        when(repo.findByIdPublicacionAndVisibilidadTrue(99)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.obtenerPublicacion(99));
    }

    @Test
    void obtenerPublicacionesPaginadas_deberiaMapearContenidoYUsarFindSeleccionIdsPorCadaElemento() {
        var p1 = new Publicacion(); p1.setIdPublicacion(1); p1.setTipoPublicacion(TipoPublicacion.EVENTO);
        Seleccion s10 = new Seleccion(); s10.setIdSeleccion(10); s10.setNombreSeleccion("Sel10");
        p1.setSelecciones(List.of(new SeleccionPublicacion(null, s10, p1)));
        
        var p2 = new Publicacion(); p2.setIdPublicacion(2); p2.setTipoPublicacion(TipoPublicacion.NOTICIA);
        Seleccion s20 = new Seleccion(); s20.setIdSeleccion(20); s20.setNombreSeleccion("Sel20");
        Seleccion s30 = new Seleccion(); s30.setIdSeleccion(30); s30.setNombreSeleccion("Sel30");
        p2.setSelecciones(List.of(new SeleccionPublicacion(null, s20, p2), new SeleccionPublicacion(null, s30, p2)));

        Page<Publicacion> page = new PageImpl<>(List.of(p1, p2), PageRequest.of(0, 5, Sort.by("fecha").descending()), 2);

        when(repo.findByVisibilidadTrue(ArgumentMatchers.any(Pageable.class))).thenReturn(page);
        when(repo.findSeleccionDtosByPublicacionId(1)).thenReturn(List.of(
            new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(10, "Sel10")
        ));
        when(repo.findSeleccionDtosByPublicacionId(2)).thenReturn(List.of(
            new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(20, "Sel20"),
            new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(30, "Sel30")
        ));

        Page<PublicacionResponse> result = service.obtenerPublicacionesPaginadas(0, 5);

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getIdSelecciones().size());
        assertEquals(10, result.getContent().get(0).getIdSelecciones().get(0).getIdSeleccion());
        assertEquals(2, result.getContent().get(1).getIdSelecciones().size());
        assertEquals(20, result.getContent().get(1).getIdSelecciones().get(0).getIdSeleccion());
        assertEquals(30, result.getContent().get(1).getIdSelecciones().get(1).getIdSeleccion());
    }

    @Test
    void obtenerPublicacionesPorTipoPaginadas_deberiaUsarFindByVisibilidadTrueAndTipoPublicacion() {
        var p = new Publicacion(); p.setIdPublicacion(5); p.setTipoPublicacion(TipoPublicacion.NOTICIA);
        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1); s1.setNombreSeleccion("Sel1");
        p.setSelecciones(List.of(new SeleccionPublicacion(null, s1, p)));
        Page<Publicacion> page = new PageImpl<>(List.of(p), PageRequest.of(0, 5), 1);

        when(repo.findByVisibilidadTrueAndTipoPublicacion(eq(TipoPublicacion.NOTICIA), any(Pageable.class))).thenReturn(page);
        when(repo.findSeleccionDtosByPublicacionId(5)).thenReturn(List.of(
            new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(1, "Sel1")
        ));

        Page<PublicacionResponse> result = service.obtenerPublicacionesPorTipoPaginadas(TipoPublicacion.NOTICIA, 0, 5);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getIdSelecciones().size());
        assertEquals(1, result.getContent().get(0).getIdSelecciones().get(0).getIdSeleccion());
        verify(repo).findByVisibilidadTrueAndTipoPublicacion(eq(TipoPublicacion.NOTICIA), any(Pageable.class));
    }
}
