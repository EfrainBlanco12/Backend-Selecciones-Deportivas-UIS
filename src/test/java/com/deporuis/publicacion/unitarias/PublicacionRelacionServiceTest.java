package com.deporuis.publicacion.unitarias;

import com.deporuis.publicacion.aplicacion.helper.PublicacionRelacionService;
import com.deporuis.publicacion.dominio.Publicacion;
import com.deporuis.seleccion.dominio.Seleccion;
import com.deporuis.seleccion.dominio.SeleccionPublicacion;
import com.deporuis.seleccion.infraestructura.SeleccionPublicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicacionRelacionServiceTest {

    @InjectMocks
    private PublicacionRelacionService service;

    @Mock
    private SeleccionPublicacionRepository seleccionPublicacionRepository;

    private Publicacion publicacion;

    private static <T> List<T> toList(Iterable<T> it) {
        List<T> out = new ArrayList<>();
        if (it != null) it.forEach(out::add);
        return out;
    }

    @BeforeEach
    void setUp() {
        publicacion = new Publicacion();
        publicacion.setIdPublicacion(100);
    }

    @Test
    void crearRelacionesSeleccion_deberiaGuardarTodasLasSelecciones() {
        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1);
        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(2);

        List<Seleccion> selecciones = List.of(s1, s2);

        List<SeleccionPublicacion> guardadas = List.of(
                new SeleccionPublicacion(null, s1, publicacion),
                new SeleccionPublicacion(null, s2, publicacion)
        );
        when(seleccionPublicacionRepository.saveAll(any())).thenReturn(guardadas);

        Iterable<SeleccionPublicacion> resultadoIt = service.crearRelacionesSeleccion(publicacion, selecciones);
        List<SeleccionPublicacion> resultado = toList(resultadoIt);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(sp -> sp.getSeleccion().getIdSeleccion().equals(1)));
        assertTrue(resultado.stream().anyMatch(sp -> sp.getSeleccion().getIdSeleccion().equals(2)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<SeleccionPublicacion>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(seleccionPublicacionRepository).saveAll(captor.capture());
        List<SeleccionPublicacion> enviados = toList(captor.getValue());
        assertEquals(2, enviados.size());
        assertTrue(enviados.stream().anyMatch(sp -> sp.getSeleccion().getIdSeleccion().equals(1)));
        assertTrue(enviados.stream().anyMatch(sp -> sp.getSeleccion().getIdSeleccion().equals(2)));
    }

    @Test
    void eliminarRelacionesSeleccion_deberiaBorrarTodoLoAsociadoALaPublicacion() {
        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1);
        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(2);

        List<SeleccionPublicacion> actuales = List.of(
                new SeleccionPublicacion(null, s1, publicacion),
                new SeleccionPublicacion(null, s2, publicacion)
        );
        when(seleccionPublicacionRepository.findAllByPublicacion(publicacion)).thenReturn(actuales);

        service.eliminarRelacionesSeleccion(publicacion);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<SeleccionPublicacion>> delCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(seleccionPublicacionRepository).deleteAll(delCaptor.capture());
        List<SeleccionPublicacion> borradas = toList(delCaptor.getValue());
        assertEquals(2, borradas.size());
        assertTrue(borradas.stream().anyMatch(sp -> sp.getSeleccion().getIdSeleccion().equals(1)));
        assertTrue(borradas.stream().anyMatch(sp -> sp.getSeleccion().getIdSeleccion().equals(2)));
    }

    @Test
    void obtenerRelacionesSeleccion_deberiaRetornarLoDelRepositorio() {
        Seleccion s = new Seleccion(); s.setIdSeleccion(10);
        List<SeleccionPublicacion> esperadas = List.of(new SeleccionPublicacion(null, s, publicacion));

        when(seleccionPublicacionRepository.findAllByPublicacion(publicacion)).thenReturn(esperadas);

        Iterable<SeleccionPublicacion> resultadoIt = service.obtenerRelacionesSeleccion(publicacion);
        List<SeleccionPublicacion> resultado = toList(resultadoIt);

        assertEquals(1, resultado.size());
        assertEquals(10, resultado.get(0).getSeleccion().getIdSeleccion());
        verify(seleccionPublicacionRepository).findAllByPublicacion(publicacion);
    }

    @Test
    void actualizarRelacionesSeleccion_deberiaEliminarNoUsadasYAgregarNuevas() {
        Seleccion s1 = new Seleccion(); s1.setIdSeleccion(1);
        Seleccion s3 = new Seleccion(); s3.setIdSeleccion(3);
        SeleccionPublicacion sp1 = new SeleccionPublicacion(null, s1, publicacion);
        SeleccionPublicacion sp3 = new SeleccionPublicacion(null, s3, publicacion);

        Seleccion s2 = new Seleccion(); s2.setIdSeleccion(2);
        SeleccionPublicacion sp2 = new SeleccionPublicacion(null, s2, publicacion);

        when(seleccionPublicacionRepository.findAllByPublicacion(publicacion))
                .thenReturn(List.of(sp1, sp3))
                .thenReturn(List.of(sp2, sp3));

        when(seleccionPublicacionRepository.saveAll(any())).thenReturn(List.of(sp2));

        Iterable<SeleccionPublicacion> resultadoIt = service.actualizarRelacionesSeleccion(
                publicacion,
                List.of(s2, s3),
                List.of(2, 3)
        );
        List<SeleccionPublicacion> resultado = toList(resultadoIt);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().anyMatch(x -> x.getSeleccion().getIdSeleccion().equals(2)));
        assertTrue(resultado.stream().anyMatch(x -> x.getSeleccion().getIdSeleccion().equals(3)));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<SeleccionPublicacion>> deleteCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(seleccionPublicacionRepository).deleteAll(deleteCaptor.capture());
        List<SeleccionPublicacion> eliminadas = toList(deleteCaptor.getValue());
        assertEquals(1, eliminadas.size());
        assertEquals(1, eliminadas.get(0).getSeleccion().getIdSeleccion());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<SeleccionPublicacion>> saveCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(seleccionPublicacionRepository, atLeastOnce()).saveAll(saveCaptor.capture());

        boolean seGuardo2 = false;
        for (Iterable<SeleccionPublicacion> it : saveCaptor.getAllValues()) {
            List<SeleccionPublicacion> guardadas = toList(it);
            if (guardadas.stream().anyMatch(sp -> sp.getSeleccion().getIdSeleccion().equals(2))) {
                seGuardo2 = true;
                break;
            }
        }
        assertTrue(seGuardo2, "Debe guardarse la nueva relación con selección=2 en alguna llamada a saveAll");

        verify(seleccionPublicacionRepository, times(2)).findAllByPublicacion(publicacion);
    }

}
