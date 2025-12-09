package com.deporuis.integrante.unitarias;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.auth.aplicacion.helper.RolVerificarExistenciaService;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.integrante.aplicacion.helper.IntegranteVerificarExistenciaService;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.excepciones.IntegranteDobleUniqueKeyException;
import com.deporuis.integrante.excepciones.IntegranteNotFoundException;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.posicion.aplicacion.helper.PosicionVerificarExistenciaService;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IntegranteVerificarExistenciaServiceTest {

    @InjectMocks
    private IntegranteVerificarExistenciaService service;

    @Mock
    private IntegranteRepository integranteRepository;

    @Mock
    private RolVerificarExistenciaService rolVerificarExistenciaService;

    @Mock
    private SeleccionVerificarExistenciaService seleccionVerificarExistenciaService;

    @Mock
    private FotoVerificarExistenciaService fotoVerificarExistenciaService;

    @Mock
    private PosicionVerificarExistenciaService posicionVerificarExistenciaService;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void verificarIntegrante_cuandoExiste_debeRetornarIntegrante() {
        Integrante integrante = new Integrante();
        integrante.setIdIntegrante(1);
        when(integranteRepository.findById(1)).thenReturn(Optional.of(integrante));

        Integrante result = service.verificarIntegrante(1);

        assertNotNull(result);
        assertEquals(1, result.getIdIntegrante());
        verify(integranteRepository).findById(1);
    }

    @Test
    void verificarIntegrante_cuandoNoExiste_debeLanzarExcepcion() {
        when(integranteRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(IntegranteNotFoundException.class, () -> service.verificarIntegrante(1));
        verify(integranteRepository).findById(1);
    }

    @Test
    void verificarCodigoIntegrante_cuandoNoExiste_debeRetornarIntegrante() {
        Integrante integrante = new Integrante();
        integrante.setCodigoUniversitario("u123");
        when(integranteRepository.findByCodigoUniversitario("U123")).thenReturn(Optional.empty());

        Integrante result = service.verificarCodigoIntegrante(integrante);

        assertNotNull(result);
        assertEquals("U123", result.getCodigoUniversitario());
        verify(integranteRepository).findByCodigoUniversitario("U123");
    }

    @Test
    void verificarCodigoIntegrante_cuandoExiste_debeLanzarExcepcion() {
        Integrante integrante = new Integrante();
        integrante.setCodigoUniversitario("u123");
        
        Integrante existente = new Integrante();
        when(integranteRepository.findByCodigoUniversitario("U123")).thenReturn(Optional.of(existente));

        assertThrows(IntegranteDobleUniqueKeyException.class, 
                () -> service.verificarCodigoIntegrante(integrante));
        verify(integranteRepository).findByCodigoUniversitario("U123");
    }

    @Test
    void verificarCorreoIntegrante_cuandoNoExiste_debeRetornarIntegrante() {
        Integrante integrante = new Integrante();
        integrante.setCorreoInstitucional("juan@correo.uis.edu.co");
        when(integranteRepository.findByCorreoInstitucional("JUAN@CORREO.UIS.EDU.CO"))
                .thenReturn(Optional.empty());

        Integrante result = service.verificarCorreoIntegrante(integrante);

        assertNotNull(result);
        assertEquals("JUAN@CORREO.UIS.EDU.CO", result.getCorreoInstitucional());
        verify(integranteRepository).findByCorreoInstitucional("JUAN@CORREO.UIS.EDU.CO");
    }

    @Test
    void verificarCorreoIntegrante_cuandoExiste_debeLanzarExcepcion() {
        Integrante integrante = new Integrante();
        integrante.setCorreoInstitucional("juan@correo.uis.edu.co");
        
        Integrante existente = new Integrante();
        when(integranteRepository.findByCorreoInstitucional("JUAN@CORREO.UIS.EDU.CO"))
                .thenReturn(Optional.of(existente));

        assertThrows(IntegranteDobleUniqueKeyException.class, 
                () -> service.verificarCorreoIntegrante(integrante));
        verify(integranteRepository).findByCorreoInstitucional("JUAN@CORREO.UIS.EDU.CO");
    }

    @Test
    void verificarCorreoCodigoIntegrante_debeVerificarAmbos() {
        Integrante integrante = new Integrante();
        integrante.setCodigoUniversitario("u123");
        integrante.setCorreoInstitucional("juan@correo.uis.edu.co");
        
        when(integranteRepository.findByCodigoUniversitario("U123")).thenReturn(Optional.empty());
        when(integranteRepository.findByCorreoInstitucional("JUAN@CORREO.UIS.EDU.CO"))
                .thenReturn(Optional.empty());

        Integrante result = service.verificarCorreoCodigoIntegrante(integrante);

        assertNotNull(result);
        assertEquals("U123", result.getCodigoUniversitario());
        assertEquals("JUAN@CORREO.UIS.EDU.CO", result.getCorreoInstitucional());
    }

    @Test
    void verificarRol_debeDelegarEnRolVerificarExistenciaService() {
        Rol rol = new Rol();
        rol.setIdRol(1);
        when(rolVerificarExistenciaService.verificarRol(1)).thenReturn(rol);

        Rol result = service.verificarRol(1);

        assertNotNull(result);
        assertEquals(1, result.getIdRol());
        verify(rolVerificarExistenciaService).verificarRol(1);
    }

    @Test
    void verificarSeleccion_debeDelegarEnSeleccionVerificarExistenciaService() {
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(1);
        when(seleccionVerificarExistenciaService.verificarSeleccion(1)).thenReturn(seleccion);

        Seleccion result = service.verificarSeleccion(1);

        assertNotNull(result);
        assertEquals(1, result.getIdSeleccion());
        verify(seleccionVerificarExistenciaService).verificarSeleccion(1);
    }

    @Test
    void verificarPosiciones_debeVerificarTodasLasPosiciones() {
        Posicion pos1 = new Posicion();
        pos1.setIdPosicion(1);
        Posicion pos2 = new Posicion();
        pos2.setIdPosicion(2);

        when(posicionVerificarExistenciaService.verificarPosiciones(List.of(1, 2)))
                .thenReturn(List.of(pos1, pos2));

        List<Posicion> result = service.verificarPosiciones(List.of(1, 2));

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(posicionVerificarExistenciaService).verificarPosiciones(List.of(1, 2));
    }
}
