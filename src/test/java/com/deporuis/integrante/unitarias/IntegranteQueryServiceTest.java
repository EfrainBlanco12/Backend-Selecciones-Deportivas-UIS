package com.deporuis.integrante.unitarias;

import com.deporuis.auth.dominio.Rol;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.integrante.aplicacion.IntegranteQueryService;
import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IntegranteQueryServiceTest {

    @InjectMocks private IntegranteQueryService service;
    @Mock private IntegranteRepository repo;
    @Mock private IntegranteVerificarExistenciaService verificar;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    private Integrante entity(int id) {
        Integrante i = new Integrante("123","Ana","Gomez", LocalDate.of(2000,1,1),1.7f,60f,10,"ana@correo.uis.edu.co");
        i.setIdIntegrante(id);
        Rol rol = new Rol();
        rol.setIdRol(3);
        i.setRol(rol);
        Seleccion sel = new Seleccion();
        sel.setIdSeleccion(1);
        i.setSeleccion(sel);
        Foto f = new Foto();
        f.setIdFoto(7);
        i.setFoto(f);
        Posicion p1 = new Posicion();
        p1.setIdPosicion(1);
        Posicion p2 = new Posicion();
        p2.setIdPosicion(2);
        List<IntegrantePosicion> ips = new ArrayList<>();
        ips.add(new IntegrantePosicion(null, i, p1));
        ips.add(new IntegrantePosicion(null, i, p2));
        i.setPosiciones(ips);
        i.setVisibilidad(true);
        return i;
    }

    @Test
    void obtenerPaginado_ok() {
        Page<Integrante> page = new PageImpl<>(List.of(entity(5)));
        when(repo.findByVisibilidadTrue(PageRequest.of(0,5))).thenReturn(page);
        Page<IntegranteResponse> out = service.obtenerIntegrantesPaginados(0,5);
        assertEquals(1, out.getTotalElements());
    }

    @Test
    void obtenerPorId_ok() {
        Integrante i = entity(8);
        when(verificar.verificarIntegrante(8)).thenReturn(i);
        IntegranteResponse out = service.obtenerIntegrante(8);
        assertEquals(8, out.getIdIntegrante());
    }
}
