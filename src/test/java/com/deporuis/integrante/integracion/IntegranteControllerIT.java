package com.deporuis.integrante.integracion;

import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.deporuis.auth.infraestructura.dto.RolResponse;
import com.deporuis.auth.infraestructura.JwtFilter;              // ⬅️ importa el filtro
import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.integrante.aplicacion.IntegranteService;
import com.deporuis.integrante.infraestructura.IntegranteController;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IntegranteController.class)
@AutoConfigureMockMvc(addFilters = false)
class IntegranteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IntegranteService integranteService;

    // ⬇️ ESTA LÍNEA ES LA CLAVE: evita que Spring cree el JwtFilter real (y por ende JwtService)
    @MockitoBean
    private JwtFilter jwtFilter;

    /**
     * Construye un IntegranteResponse de ejemplo con los tipos correctos:
     * - foto.contenido: byte[] (Jackson lo serializa como Base64)
     * - posiciones: objetos (no IDs)
     * - seleccion: solo ID
     */
    private IntegranteResponse sampleResponse(
            int id,
            int idSeleccion,
            int idRol,
            String nombreRol,
            int idFoto,
            byte[] contenidoBytes,   // byte[] aquí, NO String
            int temporada,
            int idPosicion,
            String nombrePos,
            String deporte
    ) {
        // Rol (objeto)
        RolResponse rol = new RolResponse();
        rol.setIdRol(idRol);
        rol.setNombreRol(nombreRol);

        // Foto (objeto) - contenido es byte[]
        FotoResponse foto = new FotoResponse(idFoto, contenidoBytes, temporada, null, null, null);

        // Posicion (objeto) con Deporte asociado
        Deporte deporteObj = new Deporte();
        deporteObj.setNombreDeporte(deporte);

        Posicion p1 = new Posicion();
        p1.setIdPosicion(idPosicion);
        p1.setNombrePosicion(nombrePos);
        p1.setDeporte(deporteObj);

        PosicionResponse posDto = new PosicionResponse(p1);

        // IntegranteResponse armando estructura requerida
        IntegranteResponse dto = new IntegranteResponse();
        dto.setIdIntegrante(id);
        dto.setCodigoUniversitario("100" + id);
        dto.setNombres("Nombre" + id);
        dto.setApellidos("Apellido" + id);
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setAltura(1.70f);
        dto.setPeso(70.0f);
        dto.setDorsal(10);
        dto.setCorreoUniversitario("u" + id + "@uis.edu.co");

        // Selección: solo ID
        dto.setIdSeleccion(idSeleccion);

        // Objetos
        dto.setRol(rol);
        dto.setFotos(List.of(foto));
        dto.setPosiciones(List.of(posDto));

        return dto;
    }

    @Test
    void obtenerIntegrante_debeRetornarObjetoConRolFotoPosicionesAnidados() throws Exception {
        // Usamos bytes {1,2,3} -> en Base64 es "AQID"
        byte[] fotoBytes = new byte[]{1, 2, 3};
        String expectedBase64 = Base64.getEncoder().encodeToString(fotoBytes);

        IntegranteResponse uno = sampleResponse(
                1,             // idIntegrante
                11,            // idSeleccion (solo id)
                7,  "Delantero",
                5,  fotoBytes, // bytes
                2024,
                3,  "9", "Fútbol"
        );

        Mockito.when(integranteService.obtenerIntegrante(1)).thenReturn(uno);

        mockMvc.perform(get("/private/integrante/obtener/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // básicos
                .andExpect(jsonPath("$.idIntegrante").value(1))
                .andExpect(jsonPath("$.idSeleccion").value(11))
                // rol objeto
                .andExpect(jsonPath("$.rol.idRol").value(7))
                .andExpect(jsonPath("$.rol.nombreRol").value("Delantero"))
                // foto objeto (byte[] -> Base64)
                .andExpect(jsonPath("$.foto.idFoto").value(5))
                .andExpect(jsonPath("$.foto.contenido").value(expectedBase64))
                .andExpect(jsonPath("$.foto.temporada").value(2024))
                // posiciones objeto
                .andExpect(jsonPath("$.posiciones", hasSize(1)))
                .andExpect(jsonPath("$.posiciones[0].idPosicion").value(3))
                .andExpect(jsonPath("$.posiciones[0].nombrePosicion").value("9"))
                .andExpect(jsonPath("$.posiciones[0].nombreDeporte").value("Fútbol"));
    }

    @Test
    void obtenerIntegrantesPaginados_debeRetornarPageConEstructuraAnidada() throws Exception {
        // Dos respuestas con bytes distintos
        byte[] bytes1 = new byte[]{1, 2, 3};      // "AQID"
        byte[] bytes2 = new byte[]{4, 5, 6};      // "BAUG"
        String b64_1 = Base64.getEncoder().encodeToString(bytes1);
        String b64_2 = Base64.getEncoder().encodeToString(bytes2);

        IntegranteResponse r1 = sampleResponse(1, 11, 7, "Delantero", 5, bytes1, 2024, 3, "9", "Fútbol");
        IntegranteResponse r2 = sampleResponse(2, 12, 3, "Defensa",   6, bytes2, 2023, 4, "5", "Fútbol");

        var page = new PageImpl<>(List.of(r1, r2), PageRequest.of(0, 6), 2);

        Mockito.when(integranteService.obtenerIntegrantesPaginados(0, 6)).thenReturn(page);

        mockMvc.perform(get("/private/integrante/lista")
                        .param("page", "0")
                        .param("size", "6")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // la Page se serializa con "content"
                .andExpect(jsonPath("$.content", hasSize(2)))
                // item 1
                .andExpect(jsonPath("$.content[0].idIntegrante").value(1))
                .andExpect(jsonPath("$.content[0].idSeleccion").value(11))
                .andExpect(jsonPath("$.content[0].rol.idRol").value(7))
                .andExpect(jsonPath("$.content[0].foto.idFoto").value(5))
                .andExpect(jsonPath("$.content[0].foto.contenido").value(b64_1))
                .andExpect(jsonPath("$.content[0].posiciones[0].idPosicion").value(3))
                // item 2
                .andExpect(jsonPath("$.content[1].idIntegrante").value(2))
                .andExpect(jsonPath("$.content[1].idSeleccion").value(12))
                .andExpect(jsonPath("$.content[1].rol.idRol").value(3))
                .andExpect(jsonPath("$.content[1].foto.idFoto").value(6))
                .andExpect(jsonPath("$.content[1].foto.contenido").value(b64_2))
                .andExpect(jsonPath("$.content[1].posiciones[0].idPosicion").value(4));
    }

    @Test
    void obtenerIntegrantePorCodigoUniversitario_debeRetornarIntegrante() throws Exception {
        byte[] fotoBytes = new byte[]{1, 2, 3};
        String expectedBase64 = Base64.getEncoder().encodeToString(fotoBytes);

        IntegranteResponse response = sampleResponse(
                15,            // idIntegrante
                20,            // idSeleccion
                7,  "Delantero",
                8,  fotoBytes,
                2024,
                5,  "10", "Fútbol"
        );
        response.setCodigoUniversitario("2025001");

        Mockito.when(integranteService.obtenerIntegrantePorCodigoUniversitario("2025001"))
                .thenReturn(response);

        mockMvc.perform(get("/private/integrante/codigo/{codigoUniversitario}", "2025001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idIntegrante").value(15))
                .andExpect(jsonPath("$.codigoUniversitario").value("2025001"))
                .andExpect(jsonPath("$.idSeleccion").value(20))
                .andExpect(jsonPath("$.rol.idRol").value(7))
                .andExpect(jsonPath("$.rol.nombreRol").value("Delantero"))
                .andExpect(jsonPath("$.foto.idFoto").value(8))
                .andExpect(jsonPath("$.foto.contenido").value(expectedBase64))
                .andExpect(jsonPath("$.posiciones", hasSize(1)))
                .andExpect(jsonPath("$.posiciones[0].idPosicion").value(5));
    }

    @Test
    void verificarCodigoUniversitario_codigoExiste_debeRetornarTrue() throws Exception {
        Mockito.when(integranteService.verificarCodigoUniversitarioExiste("2025001"))
                .thenReturn(true);

        mockMvc.perform(get("/private/integrante/verificar-codigo/{codigoUniversitario}", "2025001")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void verificarCodigoUniversitario_codigoNoExiste_debeRetornarFalse() throws Exception {
        Mockito.when(integranteService.verificarCodigoUniversitarioExiste("NUEVO123"))
                .thenReturn(false);

        mockMvc.perform(get("/private/integrante/verificar-codigo/{codigoUniversitario}", "NUEVO123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void verificarCorreoInstitucional_correoExiste_debeRetornarTrue() throws Exception {
        Mockito.when(integranteService.verificarCorreoInstitucionalExiste("ana@correo.uis.edu.co"))
                .thenReturn(true);

        mockMvc.perform(get("/private/integrante/verificar-correo/{correoInstitucional}", "ana@correo.uis.edu.co")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void verificarCorreoInstitucional_correoNoExiste_debeRetornarFalse() throws Exception {
        Mockito.when(integranteService.verificarCorreoInstitucionalExiste("nuevo@correo.uis.edu.co"))
                .thenReturn(false);

        mockMvc.perform(get("/private/integrante/verificar-correo/{correoInstitucional}", "nuevo@correo.uis.edu.co")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }
}
