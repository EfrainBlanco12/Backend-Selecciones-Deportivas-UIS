package com.deporuis.seleccion.unitarias;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.horario.aplicacion.HorarioCommandService;
import com.deporuis.horario.dominio.Horario;
import com.deporuis.seleccion.aplicacion.SeleccionCommandService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionRelacionService;
import com.deporuis.seleccion.aplicacion.helper.SeleccionVerificarExistenciaService;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionHorario;
import com.deporuis.seleccion.dominio.TipoSeleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeleccionCommandServiceTest {

    @InjectMocks
    private SeleccionCommandService commandService;

    @Mock
    private SeleccionVerificarExistenciaService verificarExistenciaService;

    @Mock
    private SeleccionRepository seleccionRepository;

    @Mock
    private FotoCommandService fotoCommandService;

    @Mock
    private HorarioCommandService horarioCommandService;

    @Mock
    private SeleccionRelacionService relacionService;

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
    void crearSeleccion_deberiaCrearYDevolverSeleccionResponse() {
        // Arrange
        SeleccionRequest request = new SeleccionRequest();
        request.setIdDeporte(1);
        request.setFotos(Collections.emptyList());
        request.setHorarios(Collections.emptyList());

        Deporte deporte = new Deporte();
        deporte.setIdDeporte(1);

        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(1);
        seleccion.setDeporte(deporte);

        List<Foto> fotos = Collections.emptyList();
        List<Horario> horarios = Collections.emptyList();
        List<SeleccionHorario> relaciones = Collections.emptyList();

        when(verificarExistenciaService.verificarDeporte(1)).thenReturn(deporte);
        when(seleccionRepository.save(any(Seleccion.class))).thenReturn(seleccion);
        when(fotoCommandService.crearFotosSeleccion(anyList(), eq(seleccion))).thenReturn(fotos);
        when(verificarExistenciaService.verificarFotos(fotos)).thenReturn(fotos);
        when(horarioCommandService.obtenerOcrearHorariosSeleccion(anyList())).thenReturn(horarios);
        when(verificarExistenciaService.verificarHorarios(horarios)).thenReturn(horarios);
        when(relacionService.crearRelacionesHorarios(seleccion, horarios)).thenReturn(relaciones);

        // Act
        SeleccionResponse response = commandService.crearSeleccion(request);

        // Assert
        assertNotNull(response);
        verify(verificarExistenciaService).verificarDeporte(1);
        verify(seleccionRepository).save(any(Seleccion.class));
        verify(fotoCommandService).crearFotosSeleccion(anyList(), eq(seleccion));
        verify(relacionService).crearRelacionesHorarios(seleccion, horarios);
    }

    @Test
    void actualizarSeleccion_deberiaActualizarYRetornarSeleccionResponse() {
        // Arrange
        int id = 1;
        SeleccionRequest request = new SeleccionRequest();
        request.setFechaCreacion(LocalDate.now());
        request.setNombreSeleccion("Nombre");
        request.setEspacioDeportivo("Cancha 1");
        request.setEquipo(Boolean.valueOf("Equipo A"));
        request.setTipo_seleccion(TipoSeleccion.FEMENINO);
        request.setIdDeporte(2);
        request.setFotos(Collections.emptyList());
        request.setHorarios(Collections.emptyList());

        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(id);

        Deporte deporte = new Deporte();
        deporte.setIdDeporte(2);
        List<Foto> nuevasFotos = Collections.emptyList();
        List<Horario> nuevosHorarios = Collections.emptyList();
        List<SeleccionHorario> relacionesHorario = Collections.emptyList();

        when(verificarExistenciaService.verificarSeleccion(id)).thenReturn(seleccion);
        when(verificarExistenciaService.verificarDeporte(2)).thenReturn(deporte);
        when(fotoCommandService.crearFotosSeleccion(anyList(), eq(seleccion))).thenReturn(nuevasFotos);
        when(verificarExistenciaService.verificarFotos(nuevasFotos)).thenReturn(nuevasFotos);
        when(horarioCommandService.obtenerOcrearHorariosSeleccion(anyList())).thenReturn(nuevosHorarios);
        when(verificarExistenciaService.verificarHorarios(nuevosHorarios)).thenReturn(nuevosHorarios);
        when(relacionService.crearRelacionesHorarios(seleccion, nuevosHorarios)).thenReturn(relacionesHorario);
        when(seleccionRepository.save(seleccion)).thenReturn(seleccion);

        // Act
        SeleccionResponse resultado = commandService.actualizarSeleccion(id, request);

        // Assert
        assertNotNull(resultado);
        verify(verificarExistenciaService).verificarSeleccion(id);
        verify(verificarExistenciaService).verificarDeporte(2);
        verify(fotoCommandService).eliminarFotosSeleccion(seleccion);
        verify(fotoCommandService).crearFotosSeleccion(anyList(), eq(seleccion));
        verify(relacionService).eliminarRelacionesSeleccion(seleccion);
        verify(relacionService).crearRelacionesHorarios(seleccion, nuevosHorarios);
        verify(seleccionRepository).save(seleccion);
    }

    @Test
    void eliminarSeleccion_deberiaEliminarRelacionFotosYSeleccion() {
        // Arrange
        int id = 1;
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(id);

        when(verificarExistenciaService.verificarSeleccion(id)).thenReturn(seleccion);

        // Act
        commandService.eliminarSeleccion(id);

        // Assert
        verify(verificarExistenciaService).verificarSeleccion(id);
        verify(relacionService).eliminarRelacionesSeleccion(seleccion);
        verify(fotoCommandService).eliminarFotosSeleccion(seleccion);
        verify(seleccionRepository).delete(seleccion);
    }

    @Test
    void softDeleteSeleccion_deberiaCambiarVisibilidadYGuardar() {
        // Arrange
        int id = 1;
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(id);
        seleccion.setVisibilidad(true); // Simulamos que está visible

        when(verificarExistenciaService.verificarSeleccion(id)).thenReturn(seleccion);

        // Act
        commandService.softDeleteSeleccion(id);

        // Assert
        assertFalse(seleccion.getVisibilidad(), "La visibilidad debe ser false después del soft delete");
        verify(seleccionRepository).save(seleccion);
    }
}
