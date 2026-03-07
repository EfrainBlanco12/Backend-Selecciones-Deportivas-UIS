package com.deporuis.foto.unitarias;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.integrante.dominio.Integrante;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FotoTest {

    @Test
    void constructorParcial_deberiaAsignarContenidoYTemporada() {
        byte[] contenido = "foto1.jpg".getBytes();
        Integer temporada = 2025;

        Foto foto = new Foto(contenido, temporada);

        assertArrayEquals(contenido, foto.getContenido());
        assertEquals(temporada, foto.getTemporada());
        assertNull(foto.getIdFoto());
        assertNull(foto.getSeleccion());
        assertNull(foto.getPublicacion());
        assertNull(foto.getIntegrante());
    }

    @Test
    void settersYGetters_deberianAsignarTodosLosCampos() {
        Foto foto = new Foto();
        foto.setIdFoto(10);
        foto.setContenido("image.png".getBytes());
        foto.setTemporada(2024);

        Seleccion seleccion = new Seleccion();
        Publicacion publicacion = new Publicacion();
        Integrante integrante = new Integrante();

        foto.setSeleccion(seleccion);
        foto.setPublicacion(publicacion);
        foto.setIntegrante(integrante);

        assertEquals(10, foto.getIdFoto());
        assertArrayEquals("image.png".getBytes(), foto.getContenido());
        assertEquals(2024, foto.getTemporada());
        assertEquals(seleccion, foto.getSeleccion());
        assertEquals(publicacion, foto.getPublicacion());
        assertEquals(integrante, foto.getIntegrante());
    }

    @Test
    void equalsYHashCode_deberianFuncionarConLombok() {
        Foto foto1 = new Foto();
        foto1.setIdFoto(1);
        foto1.setContenido("x".getBytes());
        foto1.setTemporada(2024);

        Foto foto2 = new Foto();
        foto2.setIdFoto(1);
        foto2.setContenido("x".getBytes());
        foto2.setTemporada(2024);

        assertEquals(foto1, foto2);
        assertEquals(foto1.hashCode(), foto2.hashCode());
    }
}
