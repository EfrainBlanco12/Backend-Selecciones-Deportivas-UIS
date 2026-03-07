package com.deporuis.foto.unitarias;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.infraestructura.IntegranteRepository;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.publicacion.infraestructura.PublicacionRepository;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FotoCommandServiceTest {

    @Mock
    private FotoRepository fotoRepository;

    @Mock
    private IntegranteRepository integranteRepository;

    @Mock
    private SeleccionRepository seleccionRepository;

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private FotoCommandService fotoCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearFoto_deberiaGuardarYRetornarFotoResponse() {
        FotoRequest request = new FotoRequest();
        request.setContenido("foto".getBytes());
        request.setTemporada(2025);

        Foto fotoConvertida = new Foto("foto".getBytes(), 2025);
        Foto fotoGuardada = new Foto("foto".getBytes(), 2025);
        fotoGuardada.setIdFoto(1);

        FotoResponse responseEsperado = new FotoResponse();
        responseEsperado.setIdFoto(1);
        responseEsperado.setContenido("foto".getBytes());
        responseEsperado.setTemporada(2025);

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            staticMock.when(() -> FotoMapper.requestToFoto(request)).thenReturn(fotoConvertida);
            when(fotoRepository.save(fotoConvertida)).thenReturn(fotoGuardada);
            staticMock.when(() -> FotoMapper.toResponse(fotoGuardada)).thenReturn(responseEsperado);

            FotoResponse resultado = fotoCommandService.crearFoto(request);

            assertNotNull(resultado);
            assertEquals(1, resultado.getIdFoto());
            assertArrayEquals("foto".getBytes(), resultado.getContenido());
            assertEquals(2025, resultado.getTemporada());
        }
    }

    @Test
    void crearFoto_deberiaRetornarNullSiMapperRetornaNull() {
        FotoRequest request = new FotoRequest();

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            staticMock.when(() -> FotoMapper.requestToFoto(request)).thenReturn(null);
            when(fotoRepository.save(null)).thenReturn(null);
            staticMock.when(() -> FotoMapper.toResponse(null)).thenReturn(null);

            FotoResponse resultado = fotoCommandService.crearFoto(request);

            assertNull(resultado);
            verify(fotoRepository).save(null);
        }
    }


    @Test
    void crearFoto_deberiaPropagarExcepcionSiRepositoryFalla() {
        FotoRequest request = new FotoRequest();
        request.setContenido("img".getBytes());
        request.setTemporada(2024);

        Foto foto = new Foto("img".getBytes(), 2024);

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            staticMock.when(() -> FotoMapper.requestToFoto(request)).thenReturn(foto);
            when(fotoRepository.save(foto)).thenThrow(new RuntimeException("Fallo en DB"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                fotoCommandService.crearFoto(request);
            });

            assertEquals("Fallo en DB", ex.getMessage());
        }
    }

    @Test
    void crearFoto_deberiaRetornarNullSiMapperDeSalidaFalla() {
        FotoRequest request = new FotoRequest();
        request.setContenido("imagen".getBytes());
        request.setTemporada(2023);

        Foto foto = new Foto("imagen".getBytes(), 2023);
        foto.setIdFoto(7);

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            staticMock.when(() -> FotoMapper.requestToFoto(request)).thenReturn(foto);
            when(fotoRepository.save(foto)).thenReturn(foto);
            staticMock.when(() -> FotoMapper.toResponse(foto)).thenReturn(null);

            FotoResponse result = fotoCommandService.crearFoto(request);
            assertNull(result);
        }
    }

    @Test
    void crearFoto_deberiaPermitirContenidoNull() {
        FotoRequest request = new FotoRequest();
        request.setContenido(null);
        request.setTemporada(2022);

        Foto foto = new Foto(null, 2022);
        Foto fotoGuardada = new Foto(null, 2022);
        fotoGuardada.setIdFoto(3);

        FotoResponse response = new FotoResponse();
        response.setIdFoto(3);
        response.setContenido(null);
        response.setTemporada(2022);

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            staticMock.when(() -> FotoMapper.requestToFoto(request)).thenReturn(foto);
            when(fotoRepository.save(foto)).thenReturn(fotoGuardada);
            staticMock.when(() -> FotoMapper.toResponse(fotoGuardada)).thenReturn(response);

            FotoResponse result = fotoCommandService.crearFoto(request);

            assertNotNull(result);
            assertEquals(3, result.getIdFoto());
            assertNull(result.getContenido());
            assertEquals(2022, result.getTemporada());
        }
    }

    @Test
    void actualizarFoto_deberiaActualizarYRetornarFotoResponse() {
        Integer idFoto = 1;
        FotoRequest request = new FotoRequest();
        request.setContenido("nueva_foto".getBytes());
        request.setTemporada(2026);
        request.setIdIntegrante(10);
        request.setIdSeleccion(null);
        request.setIdPublicacion(null);

        Foto fotoExistente = new Foto("foto_vieja".getBytes(), 2025);
        fotoExistente.setIdFoto(idFoto);

        Integrante integrante = new Integrante();
        integrante.setIdIntegrante(10);

        Foto fotoActualizada = new Foto("nueva_foto".getBytes(), 2026);
        fotoActualizada.setIdFoto(idFoto);
        fotoActualizada.setIntegrante(integrante);

        FotoResponse responseEsperado = new FotoResponse();
        responseEsperado.setIdFoto(idFoto);
        responseEsperado.setContenido("nueva_foto".getBytes());
        responseEsperado.setTemporada(2026);
        responseEsperado.setIdIntegrante(10);

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            when(fotoRepository.findById(idFoto)).thenReturn(Optional.of(fotoExistente));
            when(integranteRepository.findById(10)).thenReturn(Optional.of(integrante));
            when(fotoRepository.save(fotoExistente)).thenReturn(fotoActualizada);
            staticMock.when(() -> FotoMapper.toResponse(fotoActualizada)).thenReturn(responseEsperado);

            FotoResponse resultado = fotoCommandService.actualizarFoto(idFoto, request);

            assertNotNull(resultado);
            assertEquals(idFoto, resultado.getIdFoto());
            assertArrayEquals("nueva_foto".getBytes(), resultado.getContenido());
            assertEquals(2026, resultado.getTemporada());
            assertEquals(10, resultado.getIdIntegrante());

            verify(fotoRepository).findById(idFoto);
            verify(integranteRepository).findById(10);
            verify(fotoRepository).save(fotoExistente);
        }
    }

    @Test
    void actualizarFoto_deberiaLanzarExcepcionSiFotoNoExiste() {
        Integer idFoto = 999;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto".getBytes());
        request.setTemporada(2025);

        when(fotoRepository.findById(idFoto)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fotoCommandService.actualizarFoto(idFoto, request);
        });

        assertEquals("Foto no encontrada con id: " + idFoto, exception.getMessage());
        verify(fotoRepository).findById(idFoto);
        verify(fotoRepository, never()).save(any());
    }

    @Test
    void actualizarFoto_deberiaLanzarExcepcionSiIntegranteNoExiste() {
        Integer idFoto = 1;
        Integer idIntegrante = 999;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto".getBytes());
        request.setTemporada(2025);
        request.setIdIntegrante(idIntegrante);

        Foto fotoExistente = new Foto("foto_vieja".getBytes(), 2024);
        fotoExistente.setIdFoto(idFoto);

        when(fotoRepository.findById(idFoto)).thenReturn(Optional.of(fotoExistente));
        when(integranteRepository.findById(idIntegrante)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fotoCommandService.actualizarFoto(idFoto, request);
        });

        assertEquals("Integrante no encontrado", exception.getMessage());
        verify(fotoRepository).findById(idFoto);
        verify(integranteRepository).findById(idIntegrante);
        verify(fotoRepository, never()).save(any());
    }

    @Test
    void actualizarFoto_deberiaEliminarRelacionesSiSonNull() {
        Integer idFoto = 1;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto_actualizada".getBytes());
        request.setTemporada(2027);
        request.setIdIntegrante(null);
        request.setIdSeleccion(null);
        request.setIdPublicacion(null);

        Foto fotoExistente = new Foto("foto_vieja".getBytes(), 2025);
        fotoExistente.setIdFoto(idFoto);
        Integrante integrante = new Integrante();
        integrante.setIdIntegrante(5);
        fotoExistente.setIntegrante(integrante);

        Foto fotoActualizada = new Foto("foto_actualizada".getBytes(), 2027);
        fotoActualizada.setIdFoto(idFoto);

        FotoResponse responseEsperado = new FotoResponse();
        responseEsperado.setIdFoto(idFoto);
        responseEsperado.setContenido("foto_actualizada".getBytes());
        responseEsperado.setTemporada(2027);

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            when(fotoRepository.findById(idFoto)).thenReturn(Optional.of(fotoExistente));
            when(fotoRepository.save(fotoExistente)).thenReturn(fotoActualizada);
            staticMock.when(() -> FotoMapper.toResponse(fotoActualizada)).thenReturn(responseEsperado);

            FotoResponse resultado = fotoCommandService.actualizarFoto(idFoto, request);

            assertNotNull(resultado);
            assertEquals(idFoto, resultado.getIdFoto());
            assertNull(fotoExistente.getIntegrante());
            assertNull(fotoExistente.getSeleccion());
            assertNull(fotoExistente.getPublicacion());

            verify(fotoRepository).findById(idFoto);
            verify(fotoRepository).save(fotoExistente);
            verify(integranteRepository, never()).findById(anyInt());
        }
    }

    @Test
    void actualizarFoto_deberiaActualizarConSeleccion() {
        Integer idFoto = 1;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto".getBytes());
        request.setTemporada(2025);
        request.setIdSeleccion(20);

        Foto fotoExistente = new Foto("foto_vieja".getBytes(), 2024);
        fotoExistente.setIdFoto(idFoto);

        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(20);

        Foto fotoActualizada = new Foto("foto".getBytes(), 2025);
        fotoActualizada.setIdFoto(idFoto);
        fotoActualizada.setSeleccion(seleccion);

        FotoResponse responseEsperado = new FotoResponse();
        responseEsperado.setIdFoto(idFoto);
        responseEsperado.setIdSeleccion(20);

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            when(fotoRepository.findById(idFoto)).thenReturn(Optional.of(fotoExistente));
            when(seleccionRepository.findById(20)).thenReturn(Optional.of(seleccion));
            when(fotoRepository.save(fotoExistente)).thenReturn(fotoActualizada);
            staticMock.when(() -> FotoMapper.toResponse(fotoActualizada)).thenReturn(responseEsperado);

            FotoResponse resultado = fotoCommandService.actualizarFoto(idFoto, request);

            assertNotNull(resultado);
            assertEquals(20, resultado.getIdSeleccion());

            verify(seleccionRepository).findById(20);
            verify(fotoRepository).save(fotoExistente);
        }
    }

    @Test
    void actualizarFoto_deberiaActualizarConPublicacion() {
        Integer idFoto = 1;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto".getBytes());
        request.setTemporada(2025);
        request.setIdPublicacion(30);

        Foto fotoExistente = new Foto("foto_vieja".getBytes(), 2024);
        fotoExistente.setIdFoto(idFoto);

        Publicacion publicacion = new Publicacion();
        publicacion.setIdPublicacion(30);

        Foto fotoActualizada = new Foto("foto".getBytes(), 2025);
        fotoActualizada.setIdFoto(idFoto);
        fotoActualizada.setPublicacion(publicacion);

        FotoResponse responseEsperado = new FotoResponse();
        responseEsperado.setIdFoto(idFoto);
        responseEsperado.setIdPublicacion(30);

        try (MockedStatic<FotoMapper> staticMock = mockStatic(FotoMapper.class)) {
            when(fotoRepository.findById(idFoto)).thenReturn(Optional.of(fotoExistente));
            when(publicacionRepository.findById(30)).thenReturn(Optional.of(publicacion));
            when(fotoRepository.save(fotoExistente)).thenReturn(fotoActualizada);
            staticMock.when(() -> FotoMapper.toResponse(fotoActualizada)).thenReturn(responseEsperado);

            FotoResponse resultado = fotoCommandService.actualizarFoto(idFoto, request);

            assertNotNull(resultado);
            assertEquals(30, resultado.getIdPublicacion());

            verify(publicacionRepository).findById(30);
            verify(fotoRepository).save(fotoExistente);
        }
    }

    @Test
    void eliminarFotoPorId_deberiaEliminarFoto() {
        Integer idFoto = 1;
        Foto foto = new Foto("foto".getBytes(), 2025);
        foto.setIdFoto(idFoto);

        when(fotoRepository.findById(idFoto)).thenReturn(Optional.of(foto));
        doNothing().when(fotoRepository).delete(foto);

        fotoCommandService.eliminarFotoPorId(idFoto);

        verify(fotoRepository).findById(idFoto);
        verify(fotoRepository).delete(foto);
    }

    @Test
    void eliminarFotoPorId_deberiaLanzarExcepcionSiFotoNoExiste() {
        Integer idFoto = 999;

        when(fotoRepository.findById(idFoto)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fotoCommandService.eliminarFotoPorId(idFoto);
        });

        assertEquals("Foto no encontrada con id: 999", exception.getMessage());
        verify(fotoRepository).findById(idFoto);
        verify(fotoRepository, never()).delete(any());
    }
}
