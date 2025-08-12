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
}
