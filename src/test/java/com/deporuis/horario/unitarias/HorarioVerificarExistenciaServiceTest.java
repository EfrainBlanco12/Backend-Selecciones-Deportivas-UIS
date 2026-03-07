package com.deporuis.horario.unitarias;

import com.deporuis.horario.aplicacion.helper.HorarioVerificarExistenciaService;
import com.deporuis.horario.excepciones.HorarioNotFoundException;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.horario.infraestructura.HorarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HorarioVerificarExistenciaServiceTest {

    @InjectMocks
    private HorarioVerificarExistenciaService service;

    @Mock
    private HorarioRepository horarioRepository;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void verificarHorario_cuandoExiste_debeRetornarHorario() {
        Horario horario = new Horario();
        horario.setIdHorario(1);
        when(horarioRepository.findById(1)).thenReturn(Optional.of(horario));

        Horario result = service.verificarHorario(1);

        assertNotNull(result);
        assertEquals(1, result.getIdHorario());
        verify(horarioRepository).findById(1);
    }

    @Test
    void verificarHorario_cuandoNoExiste_debeLanzarExcepcion() {
        when(horarioRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(HorarioNotFoundException.class, () -> service.verificarHorario(1));
        verify(horarioRepository).findById(1);
    }
}
