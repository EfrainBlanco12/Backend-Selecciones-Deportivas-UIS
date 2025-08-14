package com.deporuis.logro.unitarias;

import com.deporuis.logro.aplicacion.LogroCommandService;
import com.deporuis.logro.aplicacion.helper.LogroRelacionService;
import com.deporuis.logro.aplicacion.helper.LogroVerificarExistenciaService;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.infraestructura.LogroRepository;
import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LogroCommandServiceTest {

    @InjectMocks private LogroCommandService service;
    @Mock private LogroRepository repo;
    @Mock private LogroVerificarExistenciaService verificar;
    @Mock private LogroRelacionService relacion;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    private LogroRequest req() {
        LogroRequest r = new LogroRequest();
        r.setFechaLogro(LocalDate.of(2024,5,20));
        r.setNombreLogro("Título");
        r.setDescripcionLogro("Desc");
        r.setSelecciones(java.util.List.of(1,2));
        return r;
    }

    private Logro entidad(int id) {
        Logro l = new Logro();
        l.setIdLogro(id);
        l.setFechaLogro(LocalDate.of(2024,5,20));
        l.setNombreLogro("Título");
        l.setDescripcionLogro("Desc");
        l.setSelecciones(new ArrayList<>());
        return l;
    }

    @Test
    void crearLogro_ok() {
        when(verificar.verificarSelecciones(req().getSelecciones())).thenReturn(Collections.emptyList());
        when(repo.save(any(Logro.class))).thenReturn(entidad(10));
        when(relacion.crearRelacionesSeleccion(any(Logro.class), anyList())).thenReturn(Collections.emptyList());

        LogroResponse out = service.crearLogro(req());

        assertEquals(10, out.getIdLogro());
        verify(repo).save(any(Logro.class));
        verify(relacion).crearRelacionesSeleccion(any(Logro.class), anyList());
    }

    @Test
    void actualizarLogro_ok() {
        Logro existente = entidad(3);
        when(verificar.verificarLogro(3)).thenReturn(existente);
        when(verificar.verificarSelecciones(req().getSelecciones())).thenReturn(Collections.emptyList());
        when(relacion.actualizarRelacionesSeleccion(eq(existente), anyList(), anyList())).thenReturn(Collections.emptyList());
        when(repo.save(existente)).thenReturn(existente);

        LogroResponse out = service.actualizarLogro(3, req());

        assertEquals(3, out.getIdLogro());
        verify(repo).save(existente);
    }

    @Test
    void eliminarLogro_ok() {
        Logro existente = entidad(7);
        when(verificar.verificarLogro(7)).thenReturn(existente);
        doNothing().when(relacion).eliminarRelacionesSeleccion(existente);
        doNothing().when(repo).delete(existente);

        service.eliminarLogro(7);

        verify(relacion).eliminarRelacionesSeleccion(existente);
        verify(repo).delete(existente);
    }
}
