package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.AuthQueryService;
import com.deporuis.auth.aplicacion.AuthService;
import com.deporuis.auth.dominio.Login;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.excepciones.LoginYaExisteException;
import com.deporuis.auth.infraestructura.LoginRepository;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthQueryService authQueryService;

    @Mock
    private IntegranteRepository integranteRepository;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
    void obtenerIntegrantesConLogin_delegaEnQueryService() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Integrante> pageMock = new PageImpl<>(List.of());
        
        when(authQueryService.obtenerIntegrantesConLogin(pageable)).thenReturn(pageMock);

        // Act
        Page<Integrante> resultado = authService.obtenerIntegrantesConLogin(pageable);

        // Assert
        assertNotNull(resultado);
        verify(authQueryService, times(1)).obtenerIntegrantesConLogin(pageable);
    }

    @Test
    void existeLoginPorCodigoUniversitario_loginExiste_debeRetornarTrue() {
        // Arrange
        String codigoUniversitario = "2200001";
        when(authQueryService.existeLoginPorCodigoUniversitario(codigoUniversitario)).thenReturn(true);

        // Act
        boolean resultado = authService.existeLoginPorCodigoUniversitario(codigoUniversitario);

        // Assert
        assertTrue(resultado);
        verify(authQueryService, times(1)).existeLoginPorCodigoUniversitario(codigoUniversitario);
    }

    @Test
    void existeLoginPorCodigoUniversitario_loginNoExiste_debeRetornarFalse() {
        // Arrange
        String codigoUniversitario = "2200999";
        when(authQueryService.existeLoginPorCodigoUniversitario(codigoUniversitario)).thenReturn(false);

        // Act
        boolean resultado = authService.existeLoginPorCodigoUniversitario(codigoUniversitario);

        // Assert
        assertFalse(resultado);
        verify(authQueryService, times(1)).existeLoginPorCodigoUniversitario(codigoUniversitario);
    }

    @Test
    void registrarLogin_integranteExiste_debeCrearLogin() {
        // Arrange
        Integer idIntegrante = 1;
        String password = "password123";
        String passwordEncriptada = "$2a$10$encrypted";
        String codigoUniversitario = "2200001";

        Integrante integrante = crearIntegrante(idIntegrante, codigoUniversitario);
        Login loginGuardado = new Login(codigoUniversitario, passwordEncriptada);

        when(integranteRepository.findById(idIntegrante)).thenReturn(Optional.of(integrante));
        when(authQueryService.existeLoginPorCodigoUniversitario(codigoUniversitario)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(passwordEncriptada);
        when(loginRepository.save(any(Login.class))).thenReturn(loginGuardado);

        // Act
        Login resultado = authService.registrarLogin(idIntegrante, password);

        // Assert
        assertNotNull(resultado);
        assertEquals(codigoUniversitario, resultado.getCodigoUniversitario());
        assertEquals(passwordEncriptada, resultado.getPassword());
        verify(integranteRepository, times(1)).findById(idIntegrante);
        verify(authQueryService, times(1)).existeLoginPorCodigoUniversitario(codigoUniversitario);
        verify(passwordEncoder, times(1)).encode(password);
        verify(loginRepository, times(1)).save(any(Login.class));
    }

    @Test
    void registrarLogin_integranteNoExiste_debeLanzarExcepcion() {
        // Arrange
        Integer idIntegrante = 999;
        String password = "password123";

        when(integranteRepository.findById(idIntegrante)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            authService.registrarLogin(idIntegrante, password);
        });

        verify(integranteRepository, times(1)).findById(idIntegrante);
        verify(authQueryService, never()).existeLoginPorCodigoUniversitario(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(loginRepository, never()).save(any(Login.class));
    }

    @Test
    void registrarLogin_loginYaExiste_debeLanzarExcepcion() {
        // Arrange
        Integer idIntegrante = 1;
        String password = "password123";
        String codigoUniversitario = "2200001";

        Integrante integrante = crearIntegrante(idIntegrante, codigoUniversitario);

        when(integranteRepository.findById(idIntegrante)).thenReturn(Optional.of(integrante));
        when(authQueryService.existeLoginPorCodigoUniversitario(codigoUniversitario)).thenReturn(true);

        // Act & Assert
        assertThrows(LoginYaExisteException.class, () -> {
            authService.registrarLogin(idIntegrante, password);
        });

        verify(integranteRepository, times(1)).findById(idIntegrante);
        verify(authQueryService, times(1)).existeLoginPorCodigoUniversitario(codigoUniversitario);
        verify(passwordEncoder, never()).encode(anyString());
        verify(loginRepository, never()).save(any(Login.class));
    }

    private Integrante crearIntegrante(Integer id, String codigoUniversitario) {
        Integrante integrante = new Integrante(
                codigoUniversitario,
                "Nombres",
                "Apellidos",
                LocalDate.of(2000, 1, 1),
                1.75f,
                70f,
                10,
                codigoUniversitario + "@correo.uis.edu.co"
        );
        integrante.setIdIntegrante(id);
        
        Rol rol = new Rol();
        rol.setIdRol(3);
        rol.setNombreRol("JUGADOR");
        integrante.setRol(rol);
        
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(1);
        integrante.setSeleccion(seleccion);
        
        return integrante;
    }
}
