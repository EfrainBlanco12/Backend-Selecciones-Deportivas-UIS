package com.deporuis.seleccion.unitarias;

import com.deporuis.seleccion.aplicacion.SeleccionQueryService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.aplicacion.mapper.SeleccionMapper;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeleccionQueryServiceTest {

    @InjectMocks
    private SeleccionQueryService queryService;

    @Mock
    private SeleccionRepository seleccionRepository;

    @Mock
    private SeleccionVerificarExistenciaService verificarExistenciaService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void obtenerSeleccionesPaginadas_deberiaRetornarPageDeSeleccionResponse() {
        // Arrange
        int page = 0, size = 2;
        Seleccion seleccion = new Seleccion();
        Page<Seleccion> seleccionPage = new PageImpl<>(List.of(seleccion));

        when(seleccionRepository.findByVisibilidadTrue(PageRequest.of(page, size)))
                .thenReturn(seleccionPage);

        // Simulamos el mapeo manualmente (si el Mapper no es mockeable)
        try (MockedStatic<SeleccionMapper> mockMapper = mockStatic(SeleccionMapper.class)) {
            SeleccionResponse mapped = new SeleccionResponse();
            mockMapper.when(() -> SeleccionMapper.seleccionToResponse(seleccion)).thenReturn(mapped);

            // Act
            Page<SeleccionResponse> resultado = queryService.obtenerSeleccionesPaginadas(page, size);

            // Assert
            assertEquals(1, resultado.getTotalElements());
            assertEquals(mapped, resultado.getContent().get(0));
            verify(seleccionRepository).findByVisibilidadTrue(PageRequest.of(page, size));
        }
    }

    @Test
    void obtenerSeleccion_deberiaVerificarYRetornarSeleccionResponse() {
        // Arrange
        int id = 10;
        Seleccion seleccion = new Seleccion();

        when(verificarExistenciaService.verificarSeleccion(id)).thenReturn(seleccion);

        SeleccionResponse esperado = new SeleccionResponse();

        try (MockedStatic<SeleccionMapper> mockMapper = mockStatic(SeleccionMapper.class)) {
            mockMapper.when(() -> SeleccionMapper.seleccionToResponse(seleccion)).thenReturn(esperado);

            // Act
            SeleccionResponse resultado = queryService.obtenerSeleccion(id);

            // Assert
            assertEquals(esperado, resultado);
            verify(verificarExistenciaService).verificarSeleccion(id);
        }
    }
}
