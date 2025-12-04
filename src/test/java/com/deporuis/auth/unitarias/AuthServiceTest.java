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

    @Test
    void eliminarLogin_loginExiste_debeEliminarLogin() {
        // Arrange
        String codigoUniversitario = "2200001";
        String passwordEncriptada = "$2a$10$encrypted";
        Login login = new Login(codigoUniversitario, passwordEncriptada);

        when(authQueryService.buscarLoginPorCodigoUniversitario(codigoUniversitario))
                .thenReturn(Optional.of(login));

        // Act
        authService.eliminarLogin(codigoUniversitario);

        // Assert
        verify(authQueryService, times(1)).buscarLoginPorCodigoUniversitario(codigoUniversitario);
        verify(loginRepository, times(1)).delete(login);
    }

    @Test
    void eliminarLogin_loginNoExiste_debeLanzarExcepcion() {
        // Arrange
        String codigoUniversitario = "9999999";

        when(authQueryService.buscarLoginPorCodigoUniversitario(codigoUniversitario))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            authService.eliminarLogin(codigoUniversitario);
        });

        verify(authQueryService, times(1)).buscarLoginPorCodigoUniversitario(codigoUniversitario);
        verify(loginRepository, never()).delete(any(Login.class));
    }

    @Test
    void verificarPassword_passwordCorrecta_debeRetornarTrue() {
        // Arrange
        String codigoUniversitario = "2200001";
        String passwordPlano = "password123";
        String passwordEncriptada = "$2a$10$encrypted";
        Login login = new Login(codigoUniversitario, passwordEncriptada);

        when(authQueryService.buscarLoginPorCodigoUniversitario(codigoUniversitario))
                .thenReturn(Optional.of(login));
        when(passwordEncoder.matches(passwordPlano, passwordEncriptada)).thenReturn(true);

        // Act
        boolean resultado = authService.verificarPassword(codigoUniversitario, passwordPlano);

        // Assert
        assertTrue(resultado);
        verify(authQueryService, times(1)).buscarLoginPorCodigoUniversitario(codigoUniversitario);
        verify(passwordEncoder, times(1)).matches(passwordPlano, passwordEncriptada);
    }

    @Test
    void verificarPassword_passwordIncorrecta_debeRetornarFalse() {
        // Arrange
        String codigoUniversitario = "2200001";
        String passwordPlano = "passwordIncorrecta";
        String passwordEncriptada = "$2a$10$encrypted";
        Login login = new Login(codigoUniversitario, passwordEncriptada);

        when(authQueryService.buscarLoginPorCodigoUniversitario(codigoUniversitario))
                .thenReturn(Optional.of(login));
        when(passwordEncoder.matches(passwordPlano, passwordEncriptada)).thenReturn(false);

        // Act
        boolean resultado = authService.verificarPassword(codigoUniversitario, passwordPlano);

        // Assert
        assertFalse(resultado);
        verify(authQueryService, times(1)).buscarLoginPorCodigoUniversitario(codigoUniversitario);
        verify(passwordEncoder, times(1)).matches(passwordPlano, passwordEncriptada);
    }

    @Test
    void verificarPassword_loginNoExiste_debeRetornarFalse() {
        // Arrange
        String codigoUniversitario = "9999999";
        String passwordPlano = "password123";

        when(authQueryService.buscarLoginPorCodigoUniversitario(codigoUniversitario))
                .thenReturn(Optional.empty());

        // Act
        boolean resultado = authService.verificarPassword(codigoUniversitario, passwordPlano);

        // Assert
        assertFalse(resultado);
        verify(authQueryService, times(1)).buscarLoginPorCodigoUniversitario(codigoUniversitario);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void cambiarPassword_loginExiste_debeActualizarPassword() {
        // Arrange
        String codigoUniversitario = "2200001";
        String passwordNueva = "nuevaPassword123";
        String passwordEncriptada = "$2a$10$nuevaEncriptada";

        Login loginExistente = new Login();
        loginExistente.setCodigoUniversitario(codigoUniversitario);
        loginExistente.setPassword("$2a$10$antiguaEncriptada");

        when(authQueryService.buscarLoginPorCodigoUniversitario(codigoUniversitario))
                .thenReturn(Optional.of(loginExistente));
        when(passwordEncoder.encode(passwordNueva)).thenReturn(passwordEncriptada);
        when(loginRepository.save(any(Login.class))).thenReturn(loginExistente);

        // Act
        Login resultado = authService.cambiarPassword(codigoUniversitario, passwordNueva);

        // Assert
        assertNotNull(resultado);
        assertEquals(codigoUniversitario, resultado.getCodigoUniversitario());
        assertEquals(passwordEncriptada, resultado.getPassword());
        verify(authQueryService, times(1)).buscarLoginPorCodigoUniversitario(codigoUniversitario);
        verify(passwordEncoder, times(1)).encode(passwordNueva);
        verify(loginRepository, times(1)).save(loginExistente);
    }

    @Test
    void cambiarPassword_loginNoExiste_debeLanzarExcepcion() {
        // Arrange
        String codigoUniversitario = "9999999";
        String passwordNueva = "nuevaPassword123";

        when(authQueryService.buscarLoginPorCodigoUniversitario(codigoUniversitario))
                .thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authService.cambiarPassword(codigoUniversitario, passwordNueva)
        );

        assertTrue(exception.getMessage().contains(codigoUniversitario));
        verify(authQueryService, times(1)).buscarLoginPorCodigoUniversitario(codigoUniversitario);
        verify(passwordEncoder, never()).encode(anyString());
        verify(loginRepository, never()).save(any(Login.class));
    }

    @Test
    void cambiarPassword_mismaPassword_debeLanzarExcepcion() {
        // Arrange
        String codigoUniversitario = "2200001";
        String passwordActual = "password123";
        String passwordEncriptadaActual = "$2a$10$antiguaEncriptada";

        Login loginExistente = new Login();
        loginExistente.setCodigoUniversitario(codigoUniversitario);
        loginExistente.setPassword(passwordEncriptadaActual);

        when(authQueryService.buscarLoginPorCodigoUniversitario(codigoUniversitario))
                .thenReturn(Optional.of(loginExistente));
        when(passwordEncoder.matches(passwordActual, passwordEncriptadaActual)).thenReturn(true);

        // Act & Assert
        com.deporuis.auth.excepciones.MismaPasswordException exception = assertThrows(
                com.deporuis.auth.excepciones.MismaPasswordException.class,
                () -> authService.cambiarPassword(codigoUniversitario, passwordActual)
        );

        assertTrue(exception.getMessage().contains("debe ser diferente"));
        verify(authQueryService, times(1)).buscarLoginPorCodigoUniversitario(codigoUniversitario);
        verify(passwordEncoder, times(1)).matches(passwordActual, passwordEncriptadaActual);
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
