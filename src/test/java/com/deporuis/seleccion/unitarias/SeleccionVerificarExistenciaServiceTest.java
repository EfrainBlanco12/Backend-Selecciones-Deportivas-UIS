package com.deporuis.seleccion.unitarias;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.aplicacion.helper.DeporteVerificarExistenciaService;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.excepciones.SeleccionNotFoundException;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.dominio.Horario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeleccionVerificarExistenciaServiceTest {

    @InjectMocks private SeleccionVerificarExistenciaService service;
    @Mock private SeleccionRepository repo;
    @Mock private DeporteVerificarExistenciaService deporteSvc;
    @Mock private FotoVerificarExistenciaService fotoSvc;
    @Mock private HorarioVerificarExistenciaService horarioSvc;

    @BeforeEach
    void open() { MockitoAnnotations.openMocks(this); }

    @Test
    void verificarSelecciones_ok() {
        List<Integer> ids = List.of(1, 2);
        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1);
        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(2);
        when(repo.findAllById(ids)).thenReturn(List.of(s1, s2));

        List<Seleccion> out = service.verificarSelecciones(ids);

        assertEquals(2, out.size());
        verify(repo).findAllById(ids);
    }

    @Test
    void verificarSelecciones_fallaSiFaltanRegistros() {
        List<Integer> ids = List.of(1, 2, 3);
        when(repo.findAllById(ids)).thenReturn(List.of(new Seleccion(), new Seleccion()));

        assertThrows(BadRequestException.class, () -> service.verificarSelecciones(ids));
    }

    @Test
    void verificarSeleccion_okSiExisteYVisible() {
        Seleccion s = new Seleccion();
        s.setVisibilidad(true);
        when(repo.findById(10)).thenReturn(Optional.of(s));

        Seleccion out = service.verificarSeleccion(10);

        assertSame(s, out);
        verify(repo).findById(10);
    }

    @Test
    void verificarSeleccion_lanzaSiNoExiste() {
        when(repo.findById(99)).thenReturn(Optional.empty());
        assertThrows(SeleccionNotFoundException.class, () -> service.verificarSeleccion(99));
    }

    @Test
    void verificarSeleccion_lanzaSiNoVisible() {
        Seleccion s = new Seleccion();
        s.setVisibilidad(false);
        when(repo.findById(5)).thenReturn(Optional.of(s));
        assertThrows(SeleccionNotFoundException.class, () -> service.verificarSeleccion(5));
    }

    @Test
    void delegados_verificarDeporte_fotos_horarios() {
        Deporte d = new Deporte(); d.setIdDeporte(3);
        List<Foto> fotos = List.of(new Foto());
        List<Horario> horarios = List.of(new Horario());

        when(deporteSvc.verificarDeporte(3)).thenReturn(d);
        when(fotoSvc.verificarFotos(fotos)).thenReturn(fotos);
        when(horarioSvc.verificarHorarios(horarios)).thenReturn(horarios);

        assertSame(d, service.verificarDeporte(3));
        assertEquals(fotos, service.verificarFotos(fotos));
        assertEquals(horarios, service.verificarHorarios(horarios));
    }
}
