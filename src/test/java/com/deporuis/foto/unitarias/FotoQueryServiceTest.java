package com.deporuis.foto.unitarias;

import com.deporuis.Foto.aplicacion.FotoQueryService;
import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.infraestructura.SeleccionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

class FotoQueryServiceTest {

    @Mock
    private FotoRepository fotoRepository;

    @Mock
    private FotoVerificarExistenciaService verificarExistenciaService;

    @Mock
    private SeleccionRepository seleccionRepository;

    @InjectMocks
    private FotoQueryService fotoQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerFotosPaginadas_deberiaRetornarPaginaDeFotoResponse() {
        int page = 0;
        int size = 2;
        Pageable pageable = PageRequest.of(page, size);

        Foto foto1 = new Foto();
        Foto foto2 = new Foto();
        List<Foto> fotos = List.of(foto1, foto2);
        Page<Foto> fotoPage = new PageImpl<>(fotos);

        FotoResponse response1 = new FotoResponse();
        FotoResponse response2 = new FotoResponse();

        try (MockedStatic<FotoMapper> mockedMapper = mockStatic(FotoMapper.class)) {
            when(fotoRepository.findAll(pageable)).thenReturn(fotoPage);
            mockedMapper.when(() -> FotoMapper.toResponse(foto1)).thenReturn(response1);
            mockedMapper.when(() -> FotoMapper.toResponse(foto2)).thenReturn(response2);

            Page<FotoResponse> resultado = fotoQueryService.obtenerFotosPaginadas(page, size);

            assertNotNull(resultado);
            assertEquals(2, resultado.getContent().size());
            assertTrue(resultado.getContent().containsAll(List.of(response1, response2)));
            verify(fotoRepository).findAll(pageable);
        }
    }

    @Test
    void obtenerFoto_deberiaRetornarFotoResponseCuandoExiste() {
        int id = 123;
        Foto foto = new Foto();
        FotoResponse responseEsperado = new FotoResponse();

        try (MockedStatic<FotoMapper> mockedMapper = mockStatic(FotoMapper.class)) {
            when(verificarExistenciaService.verificarFoto(id)).thenReturn(foto);
            mockedMapper.when(() -> FotoMapper.toResponse(foto)).thenReturn(responseEsperado);

            FotoResponse resultado = fotoQueryService.obtenerFoto(id);

            assertNotNull(resultado);
            assertEquals(responseEsperado, resultado);
            verify(verificarExistenciaService).verificarFoto(id);
        }
    }

    @Test
    void obtenerPrimeraFotoPorIdSeleccion_deberiaRetornarPrimeraFoto() {
        Integer idSeleccion = 1;
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(idSeleccion);

        Foto foto = new Foto("foto1".getBytes(), 2025);
        foto.setIdFoto(10);
        foto.setSeleccion(seleccion);

        FotoResponse responseEsperado = new FotoResponse();
        responseEsperado.setIdFoto(10);
        responseEsperado.setIdSeleccion(idSeleccion);

        try (MockedStatic<FotoMapper> mockedMapper = mockStatic(FotoMapper.class)) {
            when(seleccionRepository.findById(idSeleccion)).thenReturn(Optional.of(seleccion));
            when(fotoRepository.findFirstBySeleccionIdOrderByIdFotoAsc(idSeleccion)).thenReturn(Optional.of(foto));
            mockedMapper.when(() -> FotoMapper.toResponse(foto)).thenReturn(responseEsperado);

            FotoResponse resultado = fotoQueryService.obtenerPrimeraFotoPorIdSeleccion(idSeleccion);

            assertNotNull(resultado);
            assertEquals(10, resultado.getIdFoto());
            assertEquals(idSeleccion, resultado.getIdSeleccion());
            verify(seleccionRepository).findById(idSeleccion);
            verify(fotoRepository).findFirstBySeleccionIdOrderByIdFotoAsc(idSeleccion);
        }
    }

    @Test
    void obtenerPrimeraFotoPorIdSeleccion_deberiaLanzarExcepcionSiSeleccionNoExiste() {
        Integer idSeleccion = 999;

        when(seleccionRepository.findById(idSeleccion)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fotoQueryService.obtenerPrimeraFotoPorIdSeleccion(idSeleccion);
        });

        assertEquals("Selección no encontrada con id: 999", exception.getMessage());
        verify(seleccionRepository).findById(idSeleccion);
        verify(fotoRepository, never()).findFirstBySeleccionIdOrderByIdFotoAsc(anyInt());
    }

    @Test
    void obtenerPrimeraFotoPorIdSeleccion_deberiaLanzarExcepcionSiNoHayFotos() {
        Integer idSeleccion = 1;
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(idSeleccion);

        when(seleccionRepository.findById(idSeleccion)).thenReturn(Optional.of(seleccion));
        when(fotoRepository.findFirstBySeleccionIdOrderByIdFotoAsc(idSeleccion)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fotoQueryService.obtenerPrimeraFotoPorIdSeleccion(idSeleccion);
        });

        assertEquals("No se encontró ninguna foto para la selección con id: 1", exception.getMessage());
        verify(seleccionRepository).findById(idSeleccion);
        verify(fotoRepository).findFirstBySeleccionIdOrderByIdFotoAsc(idSeleccion);
    }
}
