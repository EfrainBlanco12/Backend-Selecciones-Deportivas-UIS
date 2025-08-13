package com.deporuis.publicacion.unitarias;

import com.deporuis.publicacion.aplicacion.PublicacionQueryService;
import com.deporuis.publicacion.aplicacion.helper.PublicacionVerificarExistenciaService;
import com.deporuis.publicacion.aplicacion.mapper.PublicacionMapper;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PublicacionQueryServiceTest {

    @InjectMocks private PublicacionQueryService service;
    @Mock private PublicacionRepository repo;
    @Mock private PublicacionVerificarExistenciaService verificar;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    @Test
    void obtenerPublicacion_ok() {
        Publicacion p = new Publicacion();
        p.setIdPublicacion(1);
        p.setTitulo("X");
        p.setFecha(LocalDateTime.now());
        p.setTipoPublicacion(TipoPublicacion.NOTICIA);

        when(verificar.verificarPublicacion(1)).thenReturn(p);

        PublicacionResponse r = service.obtenerPublicacion(1);

        assertNotNull(r);
        assertEquals(1, r.getIdPublicacion());
        verify(verificar).verificarPublicacion(1);
    }

    @Test
    void obtenerPublicacionesPaginadas_ok() {
        Publicacion p = new Publicacion();
        p.setIdPublicacion(2);
        p.setTitulo("Y");
        p.setFecha(LocalDateTime.now());
        p.setTipoPublicacion(TipoPublicacion.EVENTO);

        Page<Publicacion> page = new PageImpl<>(List.of(p));
        when(repo.findByVisibilidadTrue(PageRequest.of(0, 3))).thenReturn(page);

        // Usa PublicacionMapper::toResponse internamente
        Page<PublicacionResponse> out = service.obtenerPublicacionesPaginadas(0, 3);

        assertEquals(1, out.getTotalElements());
        assertEquals(2, out.getContent().get(0).getIdPublicacion());
        verify(repo).findByVisibilidadTrue(PageRequest.of(0, 3));
    }
}
