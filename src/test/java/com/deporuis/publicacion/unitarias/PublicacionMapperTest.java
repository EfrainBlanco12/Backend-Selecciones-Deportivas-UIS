package com.deporuis.publicacion.unitarias;

import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.publicacion.aplicacion.mapper.PublicacionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicacionMapperTest {

    @Test
    void toResponse_deberiaMapearCamposBasicosYIdSeleccionesDesdeRelaciones() {
        var p = new Publicacion();
        p.setIdPublicacion(1);
        p.setTitulo("T");
        p.setDescripcion("D");
        p.setLugar("L");
        p.setFecha(LocalDateTime.of(2024,1,1,9,0));
        p.setDuracion("2h");
        p.setVisibilidad(true);
        p.setTipoPublicacion(TipoPublicacion.NOTICIA);
        p.setFecha(LocalDateTime.of(2024,1,1,9,0));

        var s1 = new Seleccion(); s1.setIdSeleccion(10);
        var s2 = new Seleccion(); s2.setIdSeleccion(20);
        var sp1 = new SeleccionPublicacion(null, s1, p);
        var sp2 = new SeleccionPublicacion(null, s2, p);
        p.setSelecciones(List.of(sp1, sp2));

        PublicacionResponse dto = PublicacionMapper.toResponse(p);

        assertEquals(1, dto.getIdPublicacion());
        assertEquals("T", dto.getTitulo());
        assertEquals("D", dto.getDescripcion());
        assertEquals("L", dto.getLugar());
        assertEquals(LocalDateTime.of(2024,1,1,9,0), dto.getFecha());
        assertEquals("2h", dto.getDuracion());
        assertTrue(dto.getVisibilidad());

        assertEquals(TipoPublicacion.NOTICIA.name(), dto.getTipoPublicacion());
        assertNotNull(dto.getIdSelecciones());
        assertEquals(2, dto.getIdSelecciones().size());
        assertEquals(10, dto.getIdSelecciones().get(0).getIdSeleccion());
        assertEquals(20, dto.getIdSelecciones().get(1).getIdSeleccion());
    }

    @Test
    void requestToPublicacion_deberiaConstruirEntidadDesdeRequest() {
        var req = new PublicacionRequest();
        req.setTitulo("Inicio");
        req.setDescripcion("Desc");
        req.setLugar("Cancha A");
        req.setFecha(LocalDateTime.of(2024,1,1,9,0));
        req.setDuracion("2h");
        req.setTipoPublicacion(TipoPublicacion.EVENTO);

        Publicacion entity = PublicacionMapper.requestToPublicacion(req);

        assertEquals("Inicio", entity.getTitulo());
        assertEquals("Desc", entity.getDescripcion());
        assertEquals("Cancha A", entity.getLugar());
        assertEquals(LocalDateTime.of(2024,1,1,9,0), entity.getFecha());
        assertEquals("2h", entity.getDuracion());
        assertEquals(TipoPublicacion.EVENTO, entity.getTipoPublicacion());
    }
}
