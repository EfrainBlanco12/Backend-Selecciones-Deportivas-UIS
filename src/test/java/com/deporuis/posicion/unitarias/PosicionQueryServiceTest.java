package com.deporuis.posicion.unitarias;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.posicion.aplicacion.PosicionQueryService;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.excepciones.PosicionNotFoundException;
import com.deporuis.posicion.infraestructura.PosicionRepository;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PosicionQueryServiceTest {

    @InjectMocks private PosicionQueryService service;
    @Mock private PosicionRepository repo;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    private Posicion entidad(int id, String nombrePos, int idDep, String nombreDep) {
        Deporte d = new Deporte();
        d.setIdDeporte(idDep);
        d.setNombreDeporte(nombreDep);

        Posicion p = new Posicion();
        p.setIdPosicion(id);
        p.setNombrePosicion(nombrePos);
        p.setDeporte(d);          // <- evitar NPE en PosicionResponse
        p.setVisibilidad(true);
        return p;
    }

    @Test
    void obtenerPosicionPorDeporte_ok() {
        int idDeporte = 3;
        Posicion p = entidad(1, "Lateral", idDeporte, "Fútbol");

        when(repo.findAllByDeporte_IdDeporteAndVisibilidadTrue(idDeporte))
                .thenReturn(List.of(p));

        List<PosicionResponse> out = service.obtenerPosicionPorDeporte(idDeporte);

        assertEquals(1, out.size());
        assertEquals(1, out.get(0).getIdPosicion());
        assertEquals("Lateral", out.get(0).getNombrePosicion());
        assertEquals("Fútbol", out.get(0).getNombreDeporte());
        verify(repo).findAllByDeporte_IdDeporteAndVisibilidadTrue(idDeporte);
    }

    @Test
    void obtenerPosicionPorDeporte_lanza_siVacio() {
        when(repo.findAllByDeporte_IdDeporteAndVisibilidadTrue(8))
                .thenReturn(List.of());

        assertThrows(PosicionNotFoundException.class, () -> service.obtenerPosicionPorDeporte(8));
    }
}
