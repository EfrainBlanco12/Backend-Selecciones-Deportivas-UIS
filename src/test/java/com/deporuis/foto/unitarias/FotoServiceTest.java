package com.deporuis.foto.unitarias;

import com.deporuis.Foto.aplicacion.FotoCommandService;
import com.deporuis.Foto.aplicacion.FotoQueryService;
import com.deporuis.Foto.aplicacion.FotoService;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FotoServiceTest {

    @Mock
    private FotoCommandService fotoCommandService;

    @Mock
    private FotoQueryService fotoQueryService;

    @InjectMocks
    private FotoService fotoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearFoto_deberiaLlamarAFotoCommandServiceYRetornarResponse() {
        FotoRequest request = new FotoRequest();
        FotoResponse responseEsperado = new FotoResponse();

        when(fotoCommandService.crearFoto(request)).thenReturn(responseEsperado);

        FotoResponse resultado = fotoService.crearFoto(request);

        assertNotNull(resultado);
        assertEquals(responseEsperado, resultado);
        verify(fotoCommandService).crearFoto(request);
    }

    @Test
    void obtenerFotosPaginadas_deberiaRetornarResultadosDesdeFotoQueryService() {
        int page = 0;
        int size = 10;
        Page<FotoResponse> respuestaPaginada = new PageImpl<>(List.of(new FotoResponse()));

        when(fotoQueryService.obtenerFotosPaginadas(page, size)).thenReturn(respuestaPaginada);

        Page<FotoResponse> resultado = fotoService.obtenerFotosPaginadas(page, size);

        assertNotNull(resultado);
        assertEquals(1, resultado.getContent().size());
        verify(fotoQueryService).obtenerFotosPaginadas(page, size);
    }

    @Test
    void obtenerFoto_deberiaDelegarEnFotoQueryServiceYRetornarResponse() {
        int id = 123;
        FotoResponse responseEsperado = new FotoResponse();

        when(fotoQueryService.obtenerFoto(id)).thenReturn(responseEsperado);

        FotoResponse resultado = fotoService.obtenerFoto(id);

        assertNotNull(resultado);
        assertEquals(responseEsperado, resultado);
        verify(fotoQueryService).obtenerFoto(id);
    }

    @Test
    void actualizarFoto_deberiaLlamarAFotoCommandServiceYRetornarResponse() {
        Integer idFoto = 1;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto_actualizada".getBytes());
        request.setTemporada(2026);

        FotoResponse responseEsperado = new FotoResponse();
        responseEsperado.setIdFoto(idFoto);
        responseEsperado.setContenido("foto_actualizada".getBytes());
        responseEsperado.setTemporada(2026);

        when(fotoCommandService.actualizarFoto(idFoto, request)).thenReturn(responseEsperado);

        FotoResponse resultado = fotoService.actualizarFoto(idFoto, request);

        assertNotNull(resultado);
        assertEquals(responseEsperado, resultado);
        assertEquals(idFoto, resultado.getIdFoto());
        assertArrayEquals("foto_actualizada".getBytes(), resultado.getContenido());
        assertEquals(2026, resultado.getTemporada());
        verify(fotoCommandService).actualizarFoto(idFoto, request);
    }

    @Test
    void eliminarFoto_deberiaLlamarAFotoCommandService() {
        Integer idFoto = 1;

        doNothing().when(fotoCommandService).eliminarFotoPorId(idFoto);

        fotoService.eliminarFoto(idFoto);

        verify(fotoCommandService).eliminarFotoPorId(idFoto);
    }
}
