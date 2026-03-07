package com.deporuis.horario.unitarias;

import com.deporuis.horario.aplicacion.HorarioQueryService;
import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.dominio.DiaHorario;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.HorarioRepository;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HorarioQueryServiceTest {

    @Mock private HorarioRepository repo;
    @Mock private HorarioVerificarExistenciaService verificar;
    @InjectMocks private HorarioQueryService service;

    private Horario horario(Integer id) {
        Horario h = new Horario(DiaHorario.LUNES, LocalTime.of(8,0), LocalTime.of(10,0));
        try {
            java.lang.reflect.Field f = Horario.class.getDeclaredField("idHorario");
            f.setAccessible(true);
            f.set(h, id);
        } catch (Exception ignored) {}
        return h;
    }

    @Test
    void obtenerHorario_ok() {
        when(verificar.verificarHorario(3)).thenReturn(horario(3));
        HorarioResponse out = service.obtenerHorario(3);
        assertEquals(3, out.getIdHorario());
        assertEquals(DiaHorario.LUNES, out.getDia());
        verify(verificar).verificarHorario(3);
    }

    @Test
    void lista_ok() {
        when(repo.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(horario(4))));
        Page<HorarioResponse> page = service.obtenerHorariosPaginados(0,5);
        assertEquals(1, page.getTotalElements());
        assertEquals(4, page.getContent().get(0).getIdHorario());
        verify(repo).findAll(any(PageRequest.class));
    }
}