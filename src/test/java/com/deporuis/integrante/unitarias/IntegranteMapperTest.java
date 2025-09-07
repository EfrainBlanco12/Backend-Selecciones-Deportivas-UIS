package com.deporuis.integrante.unitarias;

import com.deporuis.Foto.dominio.Foto;
import com.deporuis.auth.dominio.Rol;
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.integrante.aplicacion.mapper.IntegranteMapper;
import com.deporuis.integrante.dominio.Integrante;
import com.deporuis.integrante.dominio.IntegrantePosicion;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.seleccion.dominio.Seleccion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegranteMapperTest {

    @Test
    void toResponse_debeMapearObjetosAnidadosYIdSeleccion() {
        // Domain: Rol
        Rol rol = new Rol();
        rol.setIdRol(7);
        rol.setNombreRol("Delantero");

        // Domain: Seleccion
        Seleccion seleccion = new Seleccion();
        seleccion.setIdSeleccion(11);

        // Domain: Foto (contenido es byte[])
        byte[] bytes = new byte[]{1, 2, 3}; // Base64 sería "AQID" cuando se serialice a JSON
        Foto foto = new Foto();
        foto.setIdFoto(5);
        foto.setContenido(bytes);
        foto.setTemporada(2024);

        // Domain: Posicion (relación mediante IntegrantePosicion)
        Deporte deporte = new Deporte();
        deporte.setNombreDeporte("Fútbol");

        Posicion p1 = new Posicion();
        p1.setIdPosicion(1);
        p1.setNombrePosicion("9");
        p1.setDeporte(deporte);

        IntegrantePosicion ip1 = new IntegrantePosicion();
        ip1.setPosicion(p1);

        // Domain: Integrante
        Integrante integrante = new Integrante(
                "1001",
                "Carlos",
                "Ramírez",
                LocalDate.of(2000, 5, 21),
                1.80f,
                75.0f,
                9,
                "carlos.ramirez@uis.edu.co"
        );
        integrante.setIdIntegrante(1);
        integrante.setRol(rol);
        integrante.setSeleccion(seleccion);
        integrante.setFoto(foto);
        integrante.setPosiciones(List.of(ip1));

        // Act
        IntegranteResponse dto = IntegranteMapper.toResponse(integrante);

        // Assert (campos simples)
        assertNotNull(dto);
        assertEquals(1, dto.getIdIntegrante());
        assertEquals("1001", dto.getCodigoUniversitario());
        assertEquals("Carlos", dto.getNombres());
        assertEquals("Ramírez", dto.getApellidos());
        assertEquals(LocalDate.of(2000, 5, 21), dto.getFechaNacimiento());
        assertEquals(1.80f, dto.getAltura());
        assertEquals(75.0f, dto.getPeso());
        assertEquals(9, dto.getDorsal());
        assertEquals("carlos.ramirez@uis.edu.co", dto.getCorreoUniversitario());

        // Selección solo id
        assertEquals(11, dto.getIdSeleccion());

        // Rol objeto
        assertNotNull(dto.getRol());
        assertEquals(7, dto.getRol().getIdRol());
        assertEquals("Delantero", dto.getRol().getNombreRol());

        // Foto objeto -> comparar bytes (no String)
        assertNotNull(dto.getFoto());
        assertEquals(5, dto.getFoto().getIdFoto());
        assertArrayEquals(bytes, dto.getFoto().getContenido());
        assertEquals(2024, dto.getFoto().getTemporada());

        // Posiciones objeto
        assertNotNull(dto.getPosiciones());
        assertEquals(1, dto.getPosiciones().size());
        assertEquals(1, dto.getPosiciones().get(0).getIdPosicion());
        assertEquals("9", dto.getPosiciones().get(0).getNombrePosicion());
        assertEquals("Fútbol", dto.getPosiciones().get(0).getNombreDeporte());
    }

    @Test
    void integranteToResponse_aliasDebeDelegarEnToResponse() {
        Integrante integrante = new Integrante();
        IntegranteResponse dto = IntegranteMapper.integranteToResponse(integrante);
        assertNotNull(dto);
    }
}
