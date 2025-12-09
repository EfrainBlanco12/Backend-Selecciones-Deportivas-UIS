package com.deporuis.horario.unitarias;

import com.deporuis.horario.aplicacion.HorarioCommandService;
import com.deporuis.horario.aplicacion.helper.HorarioRelacionService;
import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.dominio.DiaHorario;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.HorarioRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import com.deporuis.seleccion.infraestructura.SeleccionHorarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class HorarioCommandServiceTest {

    @Mock private HorarioRepository repo;
    @Mock private HorarioVerificarExistenciaService verificar;
    @Mock private HorarioRelacionService relacionService;
    @Mock private SeleccionHorarioRepository seleccionHorarioRepository;
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
        verify(relacionService, never()).crearRelacionesConSelecciones(any(), anyList());
    }

    @Test
    void crearHorario_conSelecciones_creaRelaciones() {
        HorarioRequest request = req();
        request.setIdSelecciones(List.of(1, 2, 3));

        Horario horarioGuardado = saved(15);
        when(repo.save(any(Horario.class))).thenReturn(horarioGuardado);

        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1);
        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(2);
        Seleccion s3 = new Seleccion(); s3.setIdSeleccion(3);
        SeleccionHorario sh1 = new SeleccionHorario(); sh1.setSeleccion(s1);
        SeleccionHorario sh2 = new SeleccionHorario(); sh2.setSeleccion(s2);
        SeleccionHorario sh3 = new SeleccionHorario(); sh3.setSeleccion(s3);
        List<SeleccionHorario> relaciones = List.of(sh1, sh2, sh3);
        when(relacionService.crearRelacionesConSelecciones(horarioGuardado, List.of(1, 2, 3)))
                .thenReturn(relaciones);

        HorarioResponse out = service.crearHorario(request);

        assertEquals(15, out.getIdHorario());
        verify(repo).save(any(Horario.class));
        verify(relacionService).crearRelacionesConSelecciones(horarioGuardado, List.of(1, 2, 3));
    }

    @Test
    void crearHorario_sinSelecciones_noCreRelaciones() {
        HorarioRequest request = req();
        request.setIdSelecciones(List.of());

        when(repo.save(any(Horario.class))).thenReturn(saved(20));

        HorarioResponse out = service.crearHorario(request);

        assertEquals(20, out.getIdHorario());
        verify(repo).save(any(Horario.class));
        verify(relacionService, never()).crearRelacionesConSelecciones(any(), anyList());
    }

    @Test
    void crearHorario_conUnaSeleccion_creaUnaRelacion() {
        HorarioRequest request = req();
        request.setIdSelecciones(List.of(5));

        Horario horarioGuardado = saved(25);
        when(repo.save(any(Horario.class))).thenReturn(horarioGuardado);

        Seleccion s = new Seleccion(); s.setIdSeleccion(5);
        SeleccionHorario sh = new SeleccionHorario(); sh.setSeleccion(s);
        List<SeleccionHorario> relaciones = List.of(sh);
        when(relacionService.crearRelacionesConSelecciones(horarioGuardado, List.of(5)))
                .thenReturn(relaciones);

        HorarioResponse out = service.crearHorario(request);

        assertEquals(25, out.getIdHorario());
        verify(relacionService).crearRelacionesConSelecciones(horarioGuardado, List.of(5));
    }

    @Test
    void actualizarHorario_sinSelecciones_actualizaSoloCampos() {
        HorarioRequest request = req();
        request.setDia(DiaHorario.VIERNES);
        request.setHoraInicio(LocalTime.of(14, 0));
        request.setHoraFin(LocalTime.of(16, 0));

        Horario horarioExistente = saved(10);
        when(verificar.verificarHorario(10)).thenReturn(horarioExistente);
        when(repo.save(horarioExistente)).thenReturn(horarioExistente);

        HorarioResponse out = service.actualizarHorario(10, request);

        assertEquals(10, out.getIdHorario());
        verify(verificar).verificarHorario(10);
        verify(repo).save(horarioExistente);
        verify(relacionService, never()).crearRelacionesConSelecciones(any(), anyList());
    }

    @Test
    void actualizarHorario_conSelecciones_actualizaCamposYRelaciones() {
        HorarioRequest request = req();
        request.setIdSelecciones(List.of(1, 2));

        Horario horarioExistente = saved(15);
        when(verificar.verificarHorario(15)).thenReturn(horarioExistente);
        when(repo.save(horarioExistente)).thenReturn(horarioExistente);

        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1);
        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(2);
        SeleccionHorario sh1 = new SeleccionHorario(); sh1.setSeleccion(s1);
        SeleccionHorario sh2 = new SeleccionHorario(); sh2.setSeleccion(s2);
        List<SeleccionHorario> relaciones = List.of(sh1, sh2);
        when(relacionService.crearRelacionesConSelecciones(horarioExistente, List.of(1, 2)))
                .thenReturn(relaciones);

        HorarioResponse out = service.actualizarHorario(15, request);

        assertEquals(15, out.getIdHorario());
        verify(verificar).verificarHorario(15);
        verify(relacionService).crearRelacionesConSelecciones(horarioExistente, List.of(1, 2));
        verify(repo).save(horarioExistente);
    }

    @Test
    void eliminarHorario_ok_verificaYElimina() {
        Horario horarioExistente = saved(10);
        when(verificar.verificarHorario(10)).thenReturn(horarioExistente);
        when(seleccionHorarioRepository.findAllByHorario(horarioExistente)).thenReturn(Collections.emptyList());

        service.eliminarHorario(10);

        verify(verificar).verificarHorario(10);
        verify(seleccionHorarioRepository).findAllByHorario(horarioExistente);
        verify(repo).delete(horarioExistente);
    }

    @Test
    void eliminarHorario_conRelaciones_eliminaRelacionesYHorario() {
        Horario horarioExistente = saved(15);
        List<SeleccionHorario> relaciones = List.of(
                mock(SeleccionHorario.class),
                mock(SeleccionHorario.class)
        );

        when(verificar.verificarHorario(15)).thenReturn(horarioExistente);
        when(seleccionHorarioRepository.findAllByHorario(horarioExistente)).thenReturn(relaciones);

        service.eliminarHorario(15);

        verify(verificar).verificarHorario(15);
        verify(seleccionHorarioRepository).findAllByHorario(horarioExistente);
        verify(seleccionHorarioRepository).deleteAll(relaciones);
        verify(repo).delete(horarioExistente);
    }
}
