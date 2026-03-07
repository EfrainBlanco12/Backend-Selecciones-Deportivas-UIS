package com.deporuis.publicacion.unitarias;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.aplicacion.helper.PublicacionVerificarExistenciaService;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.excepciones.PublicacionNotFoundException;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicacionVerificarExistenciaServiceTest {

    @InjectMocks private PublicacionVerificarExistenciaService service;
    @Mock private PublicacionRepository repo;
    @Mock private SeleccionVerificarExistenciaService selSvc;
    @Mock private FotoVerificarExistenciaService fotoSvc;

    @BeforeEach void open() { MockitoAnnotations.openMocks(this); }

    @Test
    void verificarSelecciones_delegaEnSeleccionService() {
        List<Integer> ids = List.of(1, 2);
        List<Seleccion> selecciones = List.of(new Seleccion(), new Seleccion());
        when(selSvc.verificarSelecciones(ids)).thenReturn(selecciones);

        assertEquals(selecciones, service.verificarSelecciones(ids));
        verify(selSvc).verificarSelecciones(ids);
    }

    @Test
    void verificarFotos_delegaEnFotoService() {
        List<Foto> in = List.of(new Foto());
        when(fotoSvc.verificarFotos(in)).thenReturn(in);

        assertEquals(in, service.verificarFotos(in));
        verify(fotoSvc).verificarFotos(in);
    }

    @Test
    void verificarPublicacion_ok_siExisteYVisible() {
        Publicacion p = new Publicacion();
        p.setVisibilidad(true);
        when(repo.findById(7)).thenReturn(Optional.of(p));

        Publicacion out = service.verificarPublicacion(7);

        assertSame(p, out);
        verify(repo).findById(7);
    }

    @Test
    void verificarPublicacion_lanza_siNoExiste() {
        when(repo.findById(99)).thenReturn(Optional.empty());
        assertThrows(PublicacionNotFoundException.class, () -> service.verificarPublicacion(99));
    }

    @Test
    void verificarPublicacion_lanza_siNoVisible() {
        Publicacion p = new Publicacion();
        p.setVisibilidad(false);
        when(repo.findById(5)).thenReturn(Optional.of(p));

        assertThrows(PublicacionNotFoundException.class, () -> service.verificarPublicacion(5));
    }
}
