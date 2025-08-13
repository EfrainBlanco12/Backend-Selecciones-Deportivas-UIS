package com.deporuis.posicion.unitarias;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.deporte.infraestructura.DeporteRepository;
import com.deporuis.posicion.aplicacion.PosicionCommandService;
import com.deporuis.posicion.aplicacion.helper.PosicionVerificarExistenciaService;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.excepciones.PosicionNotFoundException;
import com.deporuis.posicion.infraestructura.PosicionRepository;
import com.deporuis.posicion.infraestructura.dto.PosicionActualizarRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PosicionCommandServiceTest {

    @InjectMocks private PosicionCommandService service;
    @Mock private PosicionRepository posRepo;
    @Mock private DeporteRepository depRepo;
    @Mock private PosicionVerificarExistenciaService verificar;

    AutoCloseable mocks;

    @BeforeEach void open() { mocks = MockitoAnnotations.openMocks(this); }
    @AfterEach  void close() throws Exception { mocks.close(); }

    private PosicionRequest reqBase() {
        PosicionRequest r = new PosicionRequest();
        r.setNombrePosicion("Central");
        r.setIdDeporte(10);
        return r;
    }

    private Deporte deporte(int id, String nombre) {
        Deporte d = new Deporte();
        d.setIdDeporte(id);
        d.setNombreDeporte(nombre);
        return d;
    }

    @Test
    void crearPosicion_creaNueva_siNoExisteReactivable() {
        PosicionRequest req = reqBase();

        Deporte d = deporte(10, "Fútbol");
        when(depRepo.findById(10)).thenReturn(Optional.of(d));
        when(verificar.verificarPosicionNoExisteYReactivarSiAplica(req)).thenReturn(Optional.empty());

        Posicion guardada = new Posicion();
        guardada.setIdPosicion(99);
        guardada.setNombrePosicion("Central");
        guardada.setDeporte(d);
        guardada.setVisibilidad(true);

        when(posRepo.save(any(Posicion.class))).thenReturn(guardada);

        PosicionResponse out = service.crearPosicion(req);

        assertEquals(99, out.getIdPosicion());
        assertEquals("Central", out.getNombrePosicion());
        verify(posRepo).save(any(Posicion.class));
    }

    @Test
    void crearPosicion_retornaReactivada_siExisteInactiva() {
        PosicionRequest req = reqBase();

        Deporte d = deporte(10, "Fútbol");
        when(depRepo.findById(10)).thenReturn(Optional.of(d));

        Posicion reactivada = new Posicion();
        reactivada.setIdPosicion(5);
        reactivada.setNombrePosicion("Central");
        reactivada.setDeporte(d);
        reactivada.setVisibilidad(true);

        when(verificar.verificarPosicionNoExisteYReactivarSiAplica(req)).thenReturn(Optional.of(reactivada));

        PosicionResponse out = service.crearPosicion(req);

        assertEquals(5, out.getIdPosicion());
        verify(posRepo, never()).save(any());
    }

    @Test
    void actualizarPosicion_ok() {
        // Entidad existente con deporte poblado (para evitar NPE en PosicionResponse)
        Deporte d = deporte(1, "Fútbol");
        Posicion existente = new Posicion();
        existente.setIdPosicion(7);
        existente.setNombrePosicion("Extremo");
        existente.setDeporte(d);
        existente.setVisibilidad(true);

        when(posRepo.findByIdPosicionAndVisibilidadTrue(7)).thenReturn(Optional.of(existente));
        when(posRepo.save(existente)).thenReturn(existente);

        PosicionActualizarRequest req = new PosicionActualizarRequest();
        req.setNombrePosicion("Extremo Ofensivo");

        PosicionResponse out = service.actualizarPosicion(7, req);

        assertEquals("Extremo Ofensivo", out.getNombrePosicion());
        assertEquals("Fútbol", out.getNombreDeporte());
        verify(posRepo).save(existente);
    }

    @Test
    void actualizarPosicion_lanza_siNoExiste() {
        when(posRepo.findByIdPosicionAndVisibilidadTrue(100)).thenReturn(Optional.empty());
        PosicionActualizarRequest req = new PosicionActualizarRequest();
        req.setNombrePosicion("X");
        assertThrows(PosicionNotFoundException.class, () -> service.actualizarPosicion(100, req));
    }

    @Test
    void softDelete_marcaInactivo_yGuarda() {
        // Entidad encontrada con deporte poblado (por el PosicionResponse del service)
        Deporte d = deporte(1, "Fútbol");
        Posicion p = new Posicion();
        p.setIdPosicion(4);
        p.setDeporte(d);
        p.setVisibilidad(true);

        when(posRepo.findById(4)).thenReturn(Optional.of(p));
        when(posRepo.save(p)).thenReturn(p);

        PosicionResponse out = service.softDelete(4);

        assertFalse(p.getVisibilidad());
        assertEquals(4, out.getIdPosicion());
        assertEquals("Fútbol", out.getNombreDeporte());
        verify(posRepo).save(p);
    }
}
