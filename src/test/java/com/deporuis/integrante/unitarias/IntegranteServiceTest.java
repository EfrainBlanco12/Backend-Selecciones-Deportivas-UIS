package com.deporuis.integrante.unitarias;

import com.deporuis.integrante.aplicacion.IntegranteCommandService;
import com.deporuis.integrante.aplicacion.IntegranteQueryService;
import com.deporuis.integrante.aplicacion.IntegranteService;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IntegranteServiceTest {

    @InjectMocks private IntegranteService service;
    @Mock private IntegranteCommandService command;
    @Mock private IntegranteQueryService query;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    private IntegranteRequest req() {
        IntegranteRequest r = new IntegranteRequest();
        r.setCodigoUniversitario("123");
        r.setNombres("Ana");
        r.setApellidos("Gomez");
        r.setFechaNacimiento(LocalDate.of(2000,1,1));
        r.setAltura(1.7f);
        r.setPeso(60f);
        r.setDorsal(10);
        r.setCorreoInstitucional("ana@correo.uis.edu.co");
        r.setIdRol(3);
        r.setIdSeleccion(1);
        return r;
    }

    private IntegranteResponse resp(int id) {
        return new IntegranteResponse(
                id,
                "123",
                "Ana",
                "Gomez",
                LocalDate.of(2000,1,1),
                1.7f,
                60f,
                10,
                "ana@correo.uis.edu.co",
                3,
                1,
                7,
                List.of(1,2)
        );
    }

    @Test
    void crear_delegaEnCommand() {
        when(command.crearIntegrante(any())).thenReturn(resp(9));
        IntegranteResponse out = service.crearIntegrante(req());
        assertEquals(9, out.getIdIntegrante());
        verify(command).crearIntegrante(any());
    }

    @Test
    void obtenerPaginado_delegaEnQuery() {
        Page<IntegranteResponse> page = new PageImpl<>(List.of(resp(1)));
        when(query.obtenerIntegrantesPaginados(0,5)).thenReturn(page);
        Page<IntegranteResponse> out = service.obtenerIntegrantesPaginados(0,5);
        assertEquals(1, out.getTotalElements());
        verify(query).obtenerIntegrantesPaginados(0,5);
    }

    @Test
    void obtenerPorId_delegaEnQuery() {
        when(query.obtenerIntegrante(7)).thenReturn(resp(7));
        IntegranteResponse out = service.obtenerIntegrante(7);
        assertEquals(7, out.getIdIntegrante());
        verify(query).obtenerIntegrante(7);
    }

    @Test
    void softDelete_delegaEnCommand() {
        doNothing().when(command).softDeleteIntegrante(4);
        service.softDeleteIntegrante(4);
        verify(command).softDeleteIntegrante(4);
    }

    @Test
    void actualizar_delegaEnCommand() {
        when(command.actualizarIntegrante(eq(3), any())).thenReturn(resp(3));
        IntegranteResponse out = service.actualizarIntegrante(3, req());
        assertEquals(3, out.getIdIntegrante());
        verify(command).actualizarIntegrante(eq(3), any());
    }
}
