package com.deporuis.foto.unitarias;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FotoCommandServiceTest {

    @Mock
    private FotoRepository fotoRepository;

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
}
