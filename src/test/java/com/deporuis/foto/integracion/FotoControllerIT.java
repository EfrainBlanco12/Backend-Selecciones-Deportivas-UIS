package com.deporuis.foto.integracion;

import com.deporuis.Foto.aplicacion.FotoService;
import com.deporuis.Foto.infraestructura.FotoController;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.deporuis.Foto.infraestructura.dto.FotoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FotoController.class)
class FotoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FotoService fotoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ENTRENADOR"})
    void crearFoto_deberiaRetornarCreatedYResponse() throws Exception {
        FotoRequest request = new FotoRequest();
        request.setContenido("nueva".getBytes());
        request.setTemporada(2025);

        FotoResponse response = new FotoResponse();
        response.setIdFoto(1);
        response.setContenido("nueva".getBytes());
        response.setTemporada(2025);

        when(fotoService.crearFoto(any())).thenReturn(response);

        mockMvc.perform(post("/private/foto/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFoto").value(1))
                .andExpect(jsonPath("$.temporada").value(2025));

        verify(fotoService).crearFoto(any());
    }

    @Test
    void obtenerFoto_deberiaRetornarFotoSiExiste() throws Exception {
        FotoResponse response = new FotoResponse();
        response.setIdFoto(1);
        response.setTemporada(2024);
        response.setContenido("foto".getBytes());

        when(fotoService.obtenerFoto(1)).thenReturn(response);

        mockMvc.perform(get("/private/foto/obtener/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFoto").value(1))
                .andExpect(jsonPath("$.temporada").value(2024));

        verify(fotoService).obtenerFoto(1);
    }

    @Test
    void obtenerFoto_deberiaRetornarNotFoundSiEsNull() throws Exception {
        when(fotoService.obtenerFoto(999)).thenReturn(null);

        mockMvc.perform(get("/private/foto/obtener/999"))
                .andExpect(status().isNotFound());

        verify(fotoService).obtenerFoto(999);
    }

    @Test
    void obtenerFotosPaginadas_deberiaRetornarPaginaConResultados() throws Exception {
        FotoResponse response = new FotoResponse();
        response.setIdFoto(1);
        response.setTemporada(2023);
        response.setContenido("foto".getBytes());

        Page<FotoResponse> pagina = new PageImpl<>(List.of(response));

        when(fotoService.obtenerFotosPaginadas(0, 1)).thenReturn(pagina);

        mockMvc.perform(get("/private/foto/lista")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].idFoto").value(1))
                .andExpect(jsonPath("$.content[0].temporada").value(2023));

        verify(fotoService).obtenerFotosPaginadas(0, 1);
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void actualizarFoto_deberiaRetornarOkYFotoActualizada() throws Exception {
        Integer idFoto = 1;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto_actualizada".getBytes());
        request.setTemporada(2026);
        request.setIdIntegrante(10);

        FotoResponse response = new FotoResponse();
        response.setIdFoto(idFoto);
        response.setContenido("foto_actualizada".getBytes());
        response.setTemporada(2026);
        response.setIdIntegrante(10);

        when(fotoService.actualizarFoto(eq(idFoto), any(FotoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/private/foto/actualizar/" + idFoto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFoto").value(idFoto))
                .andExpect(jsonPath("$.temporada").value(2026))
                .andExpect(jsonPath("$.idIntegrante").value(10));

        verify(fotoService).actualizarFoto(eq(idFoto), any(FotoRequest.class));
    }

    @Test
    @WithMockUser(roles = {"ENTRENADOR"})
    void actualizarFoto_conRolEntrenador_deberiaPermitirAcceso() throws Exception {
        Integer idFoto = 2;
        FotoRequest request = new FotoRequest();
        request.setContenido("nueva_foto".getBytes());
        request.setTemporada(2027);

        FotoResponse response = new FotoResponse();
        response.setIdFoto(idFoto);
        response.setContenido("nueva_foto".getBytes());
        response.setTemporada(2027);

        when(fotoService.actualizarFoto(eq(idFoto), any(FotoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/private/foto/actualizar/" + idFoto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFoto").value(idFoto));

        verify(fotoService).actualizarFoto(eq(idFoto), any(FotoRequest.class));
    }

    @Test
    void actualizarFoto_sinAutenticacion_deberiaRetornarUnauthorized() throws Exception {
        Integer idFoto = 1;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto".getBytes());
        request.setTemporada(2025);

        mockMvc.perform(put("/private/foto/actualizar/" + idFoto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(fotoService, never()).actualizarFoto(anyInt(), any());
    }

    @Test
    @WithMockUser(roles = {"DEPORTISTA"})
    void actualizarFoto_conRolDeportista_deberiaDenegarAcceso() throws Exception {
        Integer idFoto = 1;
        FotoRequest request = new FotoRequest();
        request.setContenido("foto".getBytes());
        request.setTemporada(2025);

        mockMvc.perform(put("/private/foto/actualizar/" + idFoto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(fotoService, never()).actualizarFoto(anyInt(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void eliminarFoto_deberiaRetornarNoContent() throws Exception {
        Integer idFoto = 1;

        doNothing().when(fotoService).eliminarFoto(idFoto);

        mockMvc.perform(delete("/private/foto/eliminar/" + idFoto))
                .andExpect(status().isNoContent());

        verify(fotoService).eliminarFoto(idFoto);
    }

    @Test
    @WithMockUser(roles = {"ENTRENADOR"})
    void eliminarFoto_conRolEntrenador_deberiaPermitirAcceso() throws Exception {
        Integer idFoto = 1;

        doNothing().when(fotoService).eliminarFoto(idFoto);

        mockMvc.perform(delete("/private/foto/eliminar/" + idFoto))
                .andExpect(status().isNoContent());

        verify(fotoService).eliminarFoto(idFoto);
    }

    @Test
    void eliminarFoto_sinAutenticacion_deberiaRetornarUnauthorized() throws Exception {
        Integer idFoto = 1;

        mockMvc.perform(delete("/private/foto/eliminar/" + idFoto))
                .andExpect(status().isUnauthorized());

        verify(fotoService, never()).eliminarFoto(anyInt());
    }

    @Test
    @WithMockUser(roles = {"DEPORTISTA"})
    void eliminarFoto_conRolDeportista_deberiaDenegarAcceso() throws Exception {
        Integer idFoto = 1;

        mockMvc.perform(delete("/private/foto/eliminar/" + idFoto))
                .andExpect(status().isForbidden());

        verify(fotoService, never()).eliminarFoto(anyInt());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRADOR"})
    void obtenerPrimeraFotoPorIdSeleccion_deberiaRetornarFoto() throws Exception {
        Integer idSeleccion = 1;
        FotoResponse response = new FotoResponse();
        response.setIdFoto(10);
        response.setIdSeleccion(idSeleccion);
        response.setContenido("foto".getBytes());
        response.setTemporada(2025);

        when(fotoService.obtenerPrimeraFotoPorIdSeleccion(idSeleccion)).thenReturn(response);

        mockMvc.perform(get("/private/foto/getByIdSeleccion/" + idSeleccion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFoto").value(10))
                .andExpect(jsonPath("$.idSeleccion").value(idSeleccion))
                .andExpect(jsonPath("$.temporada").value(2025));

        verify(fotoService).obtenerPrimeraFotoPorIdSeleccion(idSeleccion);
    }

    @Test
    @WithMockUser(roles = {"ENTRENADOR"})
    void obtenerPrimeraFotoPorIdSeleccion_conRolEntrenador_deberiaRetornarFoto() throws Exception {
        Integer idSeleccion = 2;
        FotoResponse response = new FotoResponse();
        response.setIdFoto(5);
        response.setIdSeleccion(idSeleccion);

        when(fotoService.obtenerPrimeraFotoPorIdSeleccion(idSeleccion)).thenReturn(response);

        mockMvc.perform(get("/private/foto/getByIdSeleccion/" + idSeleccion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFoto").value(5))
                .andExpect(jsonPath("$.idSeleccion").value(idSeleccion));

        verify(fotoService).obtenerPrimeraFotoPorIdSeleccion(idSeleccion);
    }

    @Test
    @WithMockUser(roles = {"DEPORTISTA"})
    void obtenerPrimeraFotoPorIdSeleccion_conRolDeportista_deberiaRetornarFoto() throws Exception {
        Integer idSeleccion = 3;
        FotoResponse response = new FotoResponse();
        response.setIdFoto(15);
        response.setIdSeleccion(idSeleccion);

        when(fotoService.obtenerPrimeraFotoPorIdSeleccion(idSeleccion)).thenReturn(response);

        mockMvc.perform(get("/private/foto/getByIdSeleccion/" + idSeleccion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFoto").value(15));

        verify(fotoService).obtenerPrimeraFotoPorIdSeleccion(idSeleccion);
    }
}
