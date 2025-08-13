package com.deporuis.publicacion.unitarias;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.aplicacion.mapper.PublicacionMapper;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicacionMapperTest {

    @Test
    void toResponse_mapeaCamposIncluyendoListas() {
        Publicacion p = new Publicacion();
        p.setIdPublicacion(7);
        p.setTitulo("T");
        p.setDescripcion("D");
        p.setLugar("L");
        p.setFecha(LocalDateTime.of(2025, 1, 1, 10, 0));
        p.setDuracion("2h");
        p.setTipoPublicacion(TipoPublicacion.NOTICIA);

        // relaciones SeleccionPublicacion
        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(10);
        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(20);
        SeleccionPublicacion sp1 = new SeleccionPublicacion(null, s1, p);
        SeleccionPublicacion sp2 = new SeleccionPublicacion(null, s2, p);
        p.setSelecciones(List.of(sp1, sp2));

        // fotos
        Foto f1 = new Foto(); f1.setIdFoto(101);
        Foto f2 = new Foto(); f2.setIdFoto(102);
        p.setFotos(List.of(f1, f2));

        PublicacionResponse r = PublicacionMapper.toResponse(p);

        assertEquals(7, r.getIdPublicacion());
        assertEquals("T", r.getTituloPublicacion());
        assertEquals("D", r.getDescripcion());
        assertEquals("L", r.getLugar());
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), r.getFecha());
        assertEquals("2h", r.getDuracion());
        assertEquals(TipoPublicacion.NOTICIA, r.getTipoPublicacion());
        assertEquals(List.of(10, 20), r.getSelecciones());
        assertEquals(List.of(101, 102), r.getFotos());
    }

    @Test
    void requestToPublicacion_construyeEntidadConCamposBasicos() {
        PublicacionRequest req = new PublicacionRequest();
        req.setTitulo("T");
        req.setDescripcion("D");
        req.setLugar("L");
        req.setFecha(LocalDateTime.of(2025, 2, 2, 9, 30));
        req.setDuracion("90m");
        req.setTipoPublicacion(TipoPublicacion.EVENTO);

        Publicacion p = PublicacionMapper.requestToPublicacion(req);

        assertEquals("T", p.getTitulo());
        assertEquals("D", p.getDescripcion());
        assertEquals("L", p.getLugar());
        assertEquals(LocalDateTime.of(2025, 2, 2, 9, 30), p.getFecha());
        assertEquals("90m", p.getDuracion());
        assertEquals(TipoPublicacion.EVENTO, p.getTipoPublicacion());
    }
}
