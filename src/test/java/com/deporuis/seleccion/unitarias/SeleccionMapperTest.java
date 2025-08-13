package com.deporuis.seleccion.unitarias;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.dominio.TipoSeleccion;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeleccionMapperTest {

    @Test
    void requestToSeleccion_mapeaCamposBasicos() {
        SeleccionRequest req = new SeleccionRequest();
        req.setFechaCreacion(LocalDate.of(2024, 5, 10));
        req.setNombreSeleccion("Sub-20");
        req.setEspacioDeportivo("Cancha Norte");
        req.setEquipo(true);
        req.setTipo_seleccion(TipoSeleccion.MIXTO);

        Seleccion sel = SeleccionMapper.requestToSeleccion(req);

        assertEquals(req.getFechaCreacion(), sel.getFechaCreacion());
        assertEquals("Sub-20", sel.getNombreSeleccion());
        assertEquals("Cancha Norte", sel.getEspacioDeportivo());
        assertEquals(Boolean.TRUE, sel.getEquipo());
        assertEquals(TipoSeleccion.MIXTO, sel.getTipo_seleccion());
    }

    @Test
    void seleccionToResponse_mapeaTodoIncluyendoIdsAnidados() {
        Seleccion sel = new Seleccion();
        sel.setIdSeleccion(7);
        sel.setFechaCreacion(LocalDate.of(2024, 2, 1));
        sel.setNombreSeleccion("Elite");
        sel.setEspacioDeportivo("Coliseo");
        sel.setEquipo(false);
        sel.setTipo_seleccion(TipoSeleccion.FEMENINO);

        Deporte dep = new Deporte();
        // Lombok genera setters; setIdDeporte existe en tu dominio
        dep.setIdDeporte(3);
        sel.setDeporte(dep);

        Foto f1 = new Foto(); f1.setIdFoto(10); f1.setSeleccion(sel);
        Foto f2 = new Foto(); f2.setIdFoto(11); f2.setSeleccion(sel);
        sel.setFotos(List.of(f1, f2));

        Horario h1 = new Horario(); h1.setIdHorario(20);
        Horario h2 = new Horario(); h2.setIdHorario(21);
        SeleccionHorario sh1 = new SeleccionHorario(null, sel, h1);
        SeleccionHorario sh2 = new SeleccionHorario(null, sel, h2);
        sel.setHorarios(List.of(sh1, sh2));

        SeleccionResponse r = SeleccionMapper.seleccionToResponse(sel);

        assertEquals(7, r.getIdSeleccion());
        assertEquals(LocalDate.of(2024, 2, 1), r.getFechaCreacion());
        assertEquals("Elite", r.getNombreSeleccion());
        assertEquals("Coliseo", r.getEspacioDeportivo());
        assertEquals(Boolean.FALSE, r.getEquipo());
        assertEquals(TipoSeleccion.FEMENINO, r.getTipoSeleccion());
        assertEquals(3, r.getDeporte());
        assertEquals(List.of(10, 11), r.getFotos());
        assertEquals(List.of(20, 21), r.getHorarios());
    }
}
