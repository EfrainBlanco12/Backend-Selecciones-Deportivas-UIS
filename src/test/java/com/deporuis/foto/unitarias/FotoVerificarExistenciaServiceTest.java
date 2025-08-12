package com.deporuis.foto.unitarias;

import com.deporuis.Foto.aplicacion.helper.FotoVerificarExistenciaService;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.excepciones.FotoNotFoundException;
import com.deporuis.Foto.infraestructura.FotoRepository;
import com.deporuis.excepcion.common.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FotoVerificarExistenciaServiceTest {

    @Mock
    private FotoRepository fotoRepository;

    @InjectMocks
    private FotoVerificarExistenciaService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void verificarFoto_deberiaRetornarFotoCuandoExiste() {
        int id = 1;
        Foto foto = new Foto();
        when(fotoRepository.findById(id)).thenReturn(Optional.of(foto));

        Foto resultado = service.verificarFoto(id);

        assertNotNull(resultado);
        assertEquals(foto, resultado);
    }

    @Test
    void verificarFoto_deberiaLanzarExcepcionCuandoNoExiste() {
        int id = 999;
        when(fotoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(FotoNotFoundException.class, () -> service.verificarFoto(id));
    }

    @Test
    void verificarFotos_deberiaRetornarListaCuandoTodasExisten() {
        Foto foto1 = new Foto();
        foto1.setIdFoto(1);
        Foto foto2 = new Foto();
        foto2.setIdFoto(2);
        List<Foto> listaEntrada = List.of(foto1, foto2);
        List<Foto> listaEncontradas = new ArrayList<>(listaEntrada);

        when(fotoRepository.findAllById(List.of(1, 2))).thenReturn(listaEncontradas);

        List<Foto> resultado = service.verificarFotos(listaEntrada);

        assertEquals(listaEntrada.size(), resultado.size());
    }

    @Test
    void verificarFotos_deberiaLanzarExcepcionCuandoFaltanFotos() {
        Foto foto1 = new Foto();
        foto1.setIdFoto(1);
        Foto foto2 = new Foto();
        foto2.setIdFoto(2);
        List<Foto> listaEntrada = List.of(foto1, foto2);
        List<Foto> listaEncontradas = List.of(foto1); // falta una

        when(fotoRepository.findAllById(List.of(1, 2))).thenReturn(listaEncontradas);

        assertThrows(BadRequestException.class, () -> service.verificarFotos(listaEntrada));
    }

    @Test
    void verificarFotos_deberiaLanzarExcepcionCuandoListaEsVacia() {
        List<Foto> listaVacia = Collections.emptyList();

        assertThrows(BadRequestException.class, () -> service.verificarFotos(listaVacia));
    }

    @Test
    void verificarFotoIntegrante_deberiaRetornarNull() {
        Foto foto = new Foto();
        Foto resultado = service.verificarFotoIntegrante(foto);

        assertNull(resultado);
    }
}
