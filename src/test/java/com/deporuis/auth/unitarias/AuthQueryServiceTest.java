package com.deporuis.auth.unitarias;

import com.deporuis.auth.aplicacion.AuthQueryService;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.auth.infraestructura.LoginRepository;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthQueryServiceTest {

    @InjectMocks
    private AuthQueryService authQueryService;

    @Mock
    private IntegranteRepository integranteRepository;

    @Mock
    private LoginRepository loginRepository;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    private Integrante crearIntegrante(Integer id, String codigo) {
        Integrante integrante = new Integrante(
                codigo,
                "Nombres",
                "Apellidos",
                LocalDate.of(2000, 1, 1),
                1.75f,
                70f,
                10,
                codigo + "@correo.uis.edu.co"
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

    @Test
    void obtenerIntegrantesConLogin_debeRetornarPaginaDeIntegrantes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Integrante integrante1 = crearIntegrante(1, "2200001");
        integrante1.getRol().setNombreRol("ADMINISTRADOR");
        
        Integrante integrante2 = crearIntegrante(2, "2200002");
        integrante2.getRol().setNombreRol("ENTRENADOR");
        
        List<Integrante> integrantes = List.of(integrante1, integrante2);
        Page<Integrante> page = new PageImpl<>(integrantes, pageable, integrantes.size());
        
        when(integranteRepository.findIntegrantesConLogin(pageable)).thenReturn(page);

        // Act
        Page<Integrante> resultado = authQueryService.obtenerIntegrantesConLogin(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.getContent().size());
        assertEquals("2200001", resultado.getContent().get(0).getCodigoUniversitario());
        assertEquals("ADMINISTRADOR", resultado.getContent().get(0).getRol().getNombreRol());
        assertEquals("2200002", resultado.getContent().get(1).getCodigoUniversitario());
        assertEquals("ENTRENADOR", resultado.getContent().get(1).getRol().getNombreRol());
        verify(integranteRepository, times(1)).findIntegrantesConLogin(pageable);
    }

    @Test
    void obtenerIntegrantesConLogin_sinIntegrantes_debeRetornarPaginaVacia() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Integrante> pageVacia = new PageImpl<>(List.of(), pageable, 0);
        
        when(integranteRepository.findIntegrantesConLogin(pageable)).thenReturn(pageVacia);

        // Act
        Page<Integrante> resultado = authQueryService.obtenerIntegrantesConLogin(pageable);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getContent().isEmpty());
        assertEquals(0, resultado.getTotalElements());
        verify(integranteRepository, times(1)).findIntegrantesConLogin(pageable);
    }

    @Test
    void existeLoginPorCodigoUniversitario_loginExiste_debeRetornarTrue() {
        // Arrange
        String codigoUniversitario = "2200001";
        com.deporuis.auth.dominio.Login login = new com.deporuis.auth.dominio.Login();
        login.setCodigoUniversitario(codigoUniversitario);
        
        when(loginRepository.findByCodigoUniversitario(codigoUniversitario))
                .thenReturn(java.util.Optional.of(login));

        // Act
        boolean resultado = authQueryService.existeLoginPorCodigoUniversitario(codigoUniversitario);

        // Assert
        assertTrue(resultado);
        verify(loginRepository, times(1)).findByCodigoUniversitario(codigoUniversitario);
    }

    @Test
    void existeLoginPorCodigoUniversitario_loginNoExiste_debeRetornarFalse() {
        // Arrange
        String codigoUniversitario = "2200001";
        
        when(loginRepository.findByCodigoUniversitario(codigoUniversitario))
                .thenReturn(java.util.Optional.empty());

        // Act
        boolean resultado = authQueryService.existeLoginPorCodigoUniversitario(codigoUniversitario);

        // Assert
        assertFalse(resultado);
        verify(loginRepository, times(1)).findByCodigoUniversitario(codigoUniversitario);
    }
}
