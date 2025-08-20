package com.deporuis.horario.unitarias;

import com.deporuis.horario.aplicacion.HorarioCommandService;
import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.dominio.DiaHorario;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.HorarioRepository;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class HorarioCommandServiceTest {

    @Mock private HorarioRepository repo;
    @Mock private HorarioVerificarExistenciaService verificar;
    @InjectMocks private HorarioCommandService service;

    private HorarioRequest req() {
        HorarioRequest r = new HorarioRequest();
        r.setDia(DiaHorario.MARTES);
        r.setHoraInicio(LocalTime.of(9,0));
        r.setHoraFin(LocalTime.of(11,0));
        return r;
    }

    private Horario saved(Integer id) {
        Horario h = new Horario(DiaHorario.MARTES, LocalTime.of(9,0), LocalTime.of(11,0));
        try {
            var f = Horario.class.getDeclaredField("idHorario");
            f.setAccessible(true);
            f.set(h, id);
        } catch (Exception ignored) {}
        return h;
    }

    @Test
    void crearHorario_ok_guardaCuandoNoExiste() {
        lenient().when(verificar.verificarHorarioDuplicado(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(repo.save(any(Horario.class))).thenReturn(saved(10));

        HorarioResponse out = service.crearHorario(req());

        assertEquals(10, out.getIdHorario());
        verify(repo).save(any(Horario.class));
    }
}
