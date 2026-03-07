package com.deporuis.posicion.unitarias;

import com.deporuis.posicion.aplicacion.helper.PosicionVerificarExistenciaService;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.excepciones.PosicionNotFoundException;
import com.deporuis.posicion.infraestructura.PosicionRepository;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PosicionVerificarExistenciaServiceTest {

    @InjectMocks private PosicionVerificarExistenciaService service;
    @Mock private PosicionRepository repo;

    @BeforeEach void open() { MockitoAnnotations.openMocks(this); }

    @Test
    void verificarPosiciones_ok() {
        List<Integer> ids = List.of(1, 2);
        when(repo.findAllById(ids)).thenReturn(List.of(new Posicion(), new Posicion()));

        List<Posicion> out = service.verificarPosiciones(ids);

        assertEquals(2, out.size());
        verify(repo).findAllById(ids);
    }

    @Test
    void verificarPosiciones_falla_siFaltan() {
        List<Integer> ids = List.of(1, 2, 3);
        when(repo.findAllById(ids)).thenReturn(List.of(new Posicion(), new Posicion()));
        assertThrows(PosicionNotFoundException.class, () -> service.verificarPosiciones(ids));
    }

    @Test
    void verificarPosicionNoExisteYReactivarSiAplica_retornaEmpty_siNoHayCoincidencia() {
        // Este método existe en tu service (lo usa CommandService)
        PosicionRequest req = new PosicionRequest();
        req.setNombrePosicion("Ala");
        req.setIdDeporte(1);

        // No mockeamos búsquedas internas -> que termine en Optional.empty()
        Optional<Posicion> out = service.verificarPosicionNoExisteYReactivarSiAplica(req);

        assertTrue(out.isEmpty());
    }
}
