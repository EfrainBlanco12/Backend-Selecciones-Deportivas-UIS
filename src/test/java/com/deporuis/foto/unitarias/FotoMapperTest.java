package com.deporuis.foto.unitarias;

import com.deporuis.Foto.aplicacion.mapper.FotoMapper;
import com.deporuis.Foto.dominio.Foto;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FotoMapperTest {

    @Test
    void requestToFoto_deberiaMapearCorrectamente() {
        FotoRequest request = new FotoRequest();
        request.setContenido("foto.jpg".getBytes());
        request.setTemporada(2025);

        Foto resultado = FotoMapper.requestToFoto(request);

        assertNotNull(resultado);
        assertArrayEquals("foto.jpg".getBytes(), resultado.getContenido());
        assertEquals(2025, resultado.getTemporada());
    }

    @Test
    void toResponse_deberiaMapearCorrectamente() {
        Foto foto = new Foto();
        foto.setIdFoto(1);
        foto.setContenido("imagen.png".getBytes());
        foto.setTemporada(2024);

        FotoResponse response = FotoMapper.toResponse(foto);

        assertNotNull(response);
        assertEquals(1, response.getIdFoto());
        assertArrayEquals("imagen.png".getBytes(), response.getContenido());
        assertEquals(2024, response.getTemporada());
    }

    @Test
    void requestToFotosPublicacion_deberiaMapearListaYAsignarPublicacion() {
        FotoRequest request = new FotoRequest();
        request.setContenido("a.jpg".getBytes());
        request.setTemporada(2023);

        Publicacion publicacion = new Publicacion();

        List<Foto> resultado = FotoMapper.requestToFotosPublicacion(List.of(request), publicacion);

        assertEquals(1, resultado.size());
        Foto foto = resultado.get(0);
        assertArrayEquals("a.jpg".getBytes(), foto.getContenido());
        assertEquals(2023, foto.getTemporada());
        assertEquals(publicacion, foto.getPublicacion());
    }

    @Test
    void requesToFotosSeleccion_deberiaMapearListaYAsignarSeleccion() {
        FotoRequest request = new FotoRequest();
        request.setContenido("b.jpg".getBytes());
        request.setTemporada(2022);

        Seleccion seleccion = new Seleccion();

        List<Foto> resultado = FotoMapper.requesToFotosSeleccion(List.of(request), seleccion);

        assertEquals(1, resultado.size());
        Foto foto = resultado.get(0);
        assertArrayEquals("b.jpg".getBytes(), foto.getContenido());
        assertEquals(2022, foto.getTemporada());
        assertEquals(seleccion, foto.getSeleccion());
    }
}
