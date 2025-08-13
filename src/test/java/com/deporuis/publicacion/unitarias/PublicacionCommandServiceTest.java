package com.deporuis.publicacion.unitarias;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.aplicacion.PublicacionCommandService;
import com.deporuis.publicacion.aplicacion.helper.PublicacionRelacionService;
import com.deporuis.publicacion.aplicacion.helper.PublicacionVerificarExistenciaService;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PublicacionCommandServiceTest {

    @InjectMocks private PublicacionCommandService service;
    @Mock private PublicacionRepository repo;
    @Mock private PublicacionVerificarExistenciaService verificar;
    @Mock private PublicacionRelacionService relacion;
    @Mock private FotoCommandService fotoSvc;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    private PublicacionRequest reqBase() {
        PublicacionRequest r = new PublicacionRequest();
        r.setTitulo("T");
        r.setDescripcion("D");
        r.setLugar("L");
        r.setFecha(LocalDateTime.of(2025, 3, 3, 12, 0));
        r.setDuracion("60m");
        r.setTipoPublicacion(TipoPublicacion.NOTICIA);
        r.setSelecciones(List.of(1, 2));     // IDs
        r.setFotos(List.of());               // FotoRequest list; para el flujo feliz usamos vacío
        return r;
    }

    @Test
    void crearPublicacion_flujoFeliz() {
        PublicacionRequest req = reqBase();

        Publicacion persisted = new Publicacion();
        persisted.setIdPublicacion(100);
        when(repo.save(any(Publicacion.class))).thenReturn(persisted);

        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1);
        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(2);
        when(verificar.verificarSelecciones(List.of(1, 2))).thenReturn(List.of(s1, s2));

        List<SeleccionPublicacion> rel = List.of(
                new SeleccionPublicacion(null, s1, persisted),
                new SeleccionPublicacion(null, s2, persisted)
        );
        when(relacion.crearRelacionesSeleccion(persisted, List.of(s1, s2))).thenReturn(rel);

        when(fotoSvc.crearFotosPublicacion(eq(req.getFotos()), eq(persisted))).thenReturn(Collections.emptyList());
        when(verificar.verificarFotos(Collections.emptyList())).thenReturn(Collections.emptyList());

        PublicacionResponse out = service.crearPublicacion(req);

        assertNotNull(out);
        verify(repo).save(any(Publicacion.class));
        verify(relacion).crearRelacionesSeleccion(eq(persisted), anyList());
        verify(fotoSvc).crearFotosPublicacion(anyList(), eq(persisted));
        verify(verificar).verificarFotos(anyList());
    }

    @Test
    void actualizarPublicacion_actualizaCampos_relacionesYfotos() {
        int id = 7;
        Publicacion existente = new Publicacion();
        existente.setIdPublicacion(id);
        when(verificar.verificarPublicacion(id)).thenReturn(existente);

        PublicacionRequest req = reqBase();
        req.setSelecciones(List.of(3, 4));

        Seleccion n1 = new Seleccion(); n1.setIdSeleccion(3);
        Seleccion n2 = new Seleccion(); n2.setIdSeleccion(4);
        when(verificar.verificarSelecciones(List.of(3, 4))).thenReturn(List.of(n1, n2));

        when(relacion.actualizarRelacionesSeleccion(eq(existente), eq(List.of(n1, n2)), eq(List.of(3, 4))))
                .thenReturn(Collections.emptyList());

        when(fotoSvc.crearFotosPublicacion(eq(req.getFotos()), eq(existente))).thenReturn(Collections.emptyList());
        when(verificar.verificarFotos(Collections.emptyList())).thenReturn(Collections.emptyList());

        when(repo.save(existente)).thenReturn(existente);

        PublicacionResponse out = service.actualizarPublicacion(id, req);

        assertNotNull(out);
        verify(verificar).verificarPublicacion(id);
        verify(relacion).actualizarRelacionesSeleccion(eq(existente), anyList(), eq(List.of(3, 4)));
        verify(fotoSvc).eliminarFotosPublicacion(existente);
        verify(fotoSvc).crearFotosPublicacion(anyList(), eq(existente));
        verify(verificar).verificarFotos(anyList());
        verify(repo).save(existente);
    }

    @Test
    void eliminarPublicacion_eliminaRelaciones_fotos_yEntidad() {
        int id = 5;
        Publicacion p = new Publicacion();
        p.setIdPublicacion(id);
        when(verificar.verificarPublicacion(id)).thenReturn(p);

        service.eliminarPublicacion(id);

        verify(relacion).eliminarRelacionesSeleccion(p);
        verify(fotoSvc).eliminarFotosPublicacion(p);
        verify(repo).delete(p);
    }

    @Test
    void softDeletePublicacion_cambiaVisibilidad_yGuarda() {
        int id = 9;
        Publicacion p = new Publicacion();
        p.setIdPublicacion(id);
        p.setVisibilidad(true);
        when(verificar.verificarPublicacion(id)).thenReturn(p);

        service.softDeletePublicacion(id);

        assertFalse(p.getVisibilidad());
        verify(repo).save(p);
    }
}
