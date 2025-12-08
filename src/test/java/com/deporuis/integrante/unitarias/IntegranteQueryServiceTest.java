package com.deporuis.integrante.unitarias;

import com.deporuis.auth.dominio.Rol;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.integrante.aplicacion.IntegranteQueryService;
import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        i.setFotos(List.of(f));
        
        // Crear deporte para las posiciones
        com.deporuis.deporte.dominio.Deporte deporte = new com.deporuis.deporte.dominio.Deporte();
        deporte.setNombreDeporte("Fútbol");
        
        Posicion p1 = new Posicion();
        p1.setIdPosicion(1);
        p1.setNombrePosicion("9");
        p1.setDeporte(deporte);
        
        Posicion p2 = new Posicion();
        p2.setIdPosicion(2);
        p2.setNombrePosicion("5");
        p2.setDeporte(deporte);
        
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

    @Test
    void obtenerPorCodigoUniversitario_ok() {
        Integrante i = entity(10);
        i.setCodigoUniversitario("2025001");
        when(repo.findByCodigoUniversitario("2025001")).thenReturn(Optional.of(i));
        
        IntegranteResponse out = service.obtenerIntegrantePorCodigoUniversitario("2025001");
        
        assertEquals(10, out.getIdIntegrante());
        assertEquals("2025001", out.getCodigoUniversitario());
        verify(repo).findByCodigoUniversitario("2025001");
    }

    @Test
    void obtenerPorCodigoUniversitario_noExiste_lanzaExcepcion() {
        when(repo.findByCodigoUniversitario("NOEXISTE")).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, 
            () -> service.obtenerIntegrantePorCodigoUniversitario("NOEXISTE"));
        
        verify(repo).findByCodigoUniversitario("NOEXISTE");
    }

    @Test
    void verificarCodigoUniversitarioExiste_codigoExiste_retornaTrue() {
        when(repo.existsByCodigoUniversitario("2025001")).thenReturn(true);
        
        Boolean resultado = service.verificarCodigoUniversitarioExiste("2025001");
        
        assertEquals(true, resultado);
        verify(repo).existsByCodigoUniversitario("2025001");
    }

    @Test
    void verificarCodigoUniversitarioExiste_codigoNoExiste_retornaFalse() {
        when(repo.existsByCodigoUniversitario("NUEVO123")).thenReturn(false);
        
        Boolean resultado = service.verificarCodigoUniversitarioExiste("NUEVO123");
        
        assertEquals(false, resultado);
        verify(repo).existsByCodigoUniversitario("NUEVO123");
    }

    @Test
    void verificarCorreoInstitucionalExiste_correoExiste_retornaTrue() {
        when(repo.existsByCorreoInstitucional("ana@correo.uis.edu.co")).thenReturn(true);
        
        Boolean resultado = service.verificarCorreoInstitucionalExiste("ana@correo.uis.edu.co");
        
        assertEquals(true, resultado);
        verify(repo).existsByCorreoInstitucional("ana@correo.uis.edu.co");
    }

    @Test
    void verificarCorreoInstitucionalExiste_correoNoExiste_retornaFalse() {
        when(repo.existsByCorreoInstitucional("nuevo@correo.uis.edu.co")).thenReturn(false);
        
        Boolean resultado = service.verificarCorreoInstitucionalExiste("nuevo@correo.uis.edu.co");
        
        assertEquals(false, resultado);
        verify(repo).existsByCorreoInstitucional("nuevo@correo.uis.edu.co");
    }
}
