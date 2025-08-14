package com.deporuis.logro.unitarias;

import com.deporuis.logro.aplicacion.LogroQueryService;
import com.deporuis.logro.aplicacion.helper.LogroVerificarExistenciaService;
import com.deporuis.logro.aplicacion.mapper.LogroMapper;
import com.deporuis.logro.dominio.Logro;
import com.deporuis.logro.infraestructura.LogroRepository;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LogroQueryServiceTest {

    @InjectMocks private LogroQueryService service;
    @Mock private LogroRepository repo;
    @Mock private LogroVerificarExistenciaService verificador;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    private Logro entidad(int id) {
        Logro l = new Logro();
        l.setIdLogro(id);
        l.setFechaLogro(LocalDate.of(2024,5,20));
        l.setNombreLogro("Título");
        l.setDescripcionLogro("Desc");
        l.setSelecciones(new java.util.ArrayList<>());
        return l;
    }

    @Test
    void obtenerLogro_ok() {
        Logro l = entidad(5);
        when(verificador.verificarLogro(5)).thenReturn(l);
        LogroResponse out = service.obtenerLogro(5);
        assertEquals(5, out.getIdLogro());
        assertEquals("Título", out.getNombreLogro());
    }

    @Test
    void obtenerPaginado_ok() {
        org.springframework.data.domain.Page<Logro> page =
                new org.springframework.data.domain.PageImpl<>(List.of(entidad(1), entidad(2)));
        when(repo.findAll(org.springframework.data.domain.PageRequest.of(0,5))).thenReturn(page);
        org.springframework.data.domain.Page<LogroResponse> out = service.obtenerLogrosPaginados(0,5);
        assertEquals(2, out.getContent().size());
    }
}
