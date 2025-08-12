package com.deporuis.foto.unitarias;

import com.deporuis.Foto.aplicacion.FotoQueryService;
import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

class FotoQueryServiceTest {

    @Mock
    private FotoRepository fotoRepository;

    @Mock
    private FotoVerificarExistenciaService verificarExistenciaService;

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
}
