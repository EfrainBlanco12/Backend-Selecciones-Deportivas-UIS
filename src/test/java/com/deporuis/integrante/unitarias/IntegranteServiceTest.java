package com.deporuis.integrante.unitarias;

import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.integrante.aplicacion.IntegranteCommandService;
import com.deporuis.integrante.aplicacion.IntegranteQueryService;
import com.deporuis.integrante.aplicacion.IntegranteService;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest; // <- No se usa aquí

import java.time.LocalDate;
import java.util.Base64;
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
        // Si tu IntegranteRequest exige foto/posiciones para crear/actualizar,
        // añádelos aquí según tu clase (p.ej. r.setFoto(...), r.setIdPosiciones(...))
        return r;
    }

    private PosicionResponse posResp(int id, String nombrePos, String deporte) {
        Deporte dep = new Deporte();
        dep.setNombreDeporte(deporte);

        Posicion p = new Posicion();
        p.setIdPosicion(id);
        p.setNombrePosicion(nombrePos);
        p.setDeporte(dep);

        return new PosicionResponse(p);
    }

    private IntegranteResponse resp(int id) {
        // Construimos el DTO con setters (evita errores de orden de @AllArgsConstructor)
        IntegranteResponse dto = new IntegranteResponse();
        dto.setIdIntegrante(id);
        dto.setCodigoUniversitario("123");
        dto.setNombres("Ana");
        dto.setApellidos("Gomez");
        dto.setFechaNacimiento(LocalDate.of(2000,1,1));
        dto.setAltura(1.7f);
        dto.setPeso(60f);
        dto.setDorsal(10);
        dto.setCorreoUniversitario("ana@correo.uis.edu.co");

        // Solo id para selección
        dto.setIdSeleccion(1);

        // Objeto para rol
        RolResponse rol = new RolResponse();
        rol.setIdRol(3);
        rol.setNombreRol("Volante");
        dto.setRol(rol);

        // Lista de objetos para fotos (byte[]; Jackson lo serializa a Base64)
        byte[] bytes = new byte[]{1,2,3}; // "AQID"
        dto.setFotos(List.of(new FotoResponse(7, bytes, 2024, id, 1, null)));

        // Lista de objetos para posiciones
        dto.setPosiciones(List.of(
                posResp(1, "9", "Fútbol"),
                posResp(2, "5", "Fútbol")
        ));

        return dto;
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

    @Test
    void obtenerPorCodigoUniversitario_delegaEnQuery() {
        when(query.obtenerIntegrantePorCodigoUniversitario("2025001")).thenReturn(resp(10));
        IntegranteResponse out = service.obtenerIntegrantePorCodigoUniversitario("2025001");
        assertEquals(10, out.getIdIntegrante());
        verify(query).obtenerIntegrantePorCodigoUniversitario("2025001");
    }
}
