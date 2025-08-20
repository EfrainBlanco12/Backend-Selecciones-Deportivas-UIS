package com.deporuis.deporte.integracion;

import com.deporuis.DeporuisApplication;
import com.deporuis.deporte.infraestructura.dto.DeporteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DeporuisApplication.class)
@AutoConfigureMockMvc
class DeporteControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /private/deporte/crear -> 201 (Created) con nombre único")
    @WithMockUser(roles = {"ENTRENADOR"})
    void crearDeporte_deberiaRetornarCreatedYResponse() throws Exception {
        String nombreUnico = "Fútbol IT " + System.nanoTime();

        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte(nombreUnico);
        req.setDescripcionDeporte("Desc");

        mockMvc.perform(post("/private/deporte/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idDeporte").exists())
                .andExpect(jsonPath("$.nombreDeporte").value(nombreUnico));
    }

    @Test
    @DisplayName("GET /private/deporte/lista -> 200 y body es un arreglo JSON (puede venir vacío)")
    @WithMockUser(roles = {"ENTRENADOR"})
    void listar_deberiaRetornarLista() throws Exception {
        mockMvc.perform(get("/private/deporte/lista")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("PUT /private/deporte/actualizar/{id} -> 200 y JSON con id/nombre (sin IDs fijos)")
    void actualizar_deberiaRetornarOk() throws Exception {
        String nombreCreado = "Dep-Actualizar " + System.nanoTime();
        DeporteRequest crear = new DeporteRequest();
        crear.setNombreDeporte(nombreCreado);
        crear.setDescripcionDeporte("Desc");

        String bodyCreado = mockMvc.perform(post("/private/deporte/crear")
                        .with(user("coach").roles("ENTRENADOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int id = objectMapper.readTree(bodyCreado).get("idDeporte").asInt();

        String nombreNuevoUnico = "Nuevo " + System.nanoTime();

        DeporteRequest actualizar = new DeporteRequest();
        actualizar.setNombreDeporte(nombreNuevoUnico);
        actualizar.setDescripcionDeporte("Desc");

        mockMvc.perform(put("/private/deporte/actualizar/{id}", id)
                        .with(user("coach").roles("ENTRENADOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizar)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idDeporte").value(id))
                .andExpect(jsonPath("$.nombreDeporte").value(nombreNuevoUnico));
    }


    @Test
    @DisplayName("PUT /private/deporte/softdelete/{id} -> 200 y JSON con id (sin IDs fijos)")
    void softDelete_deberiaRetornarOk() throws Exception {
        String nombre = "Dep-SoftDelete " + System.nanoTime();
        DeporteRequest crear = new DeporteRequest();
        crear.setNombreDeporte(nombre);
        crear.setDescripcionDeporte("Desc");

        ResultActions createResult = mockMvc.perform(post("/private/deporte/crear")
                        .with(user("coach").roles("ENTRENADOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crear)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String body = createResult.andReturn().getResponse().getContentAsString();
        int id = objectMapper.readTree(body).get("idDeporte").asInt();

        mockMvc.perform(put("/private/deporte/softdelete/{id}", id)
                        .with(user("admin").roles("ADMINISTRADOR"))
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idDeporte").value(id));
    }

    @Test
    @DisplayName("POST /private/deporte/crear (duplicado) -> 409 Conflict")
    @WithMockUser(roles = {"ENTRENADOR"})
    void crearDeporte_duplicado_deberiaRetornarConflict() throws Exception {
        String nombre = "Fútbol IT Dup " + System.nanoTime();

        DeporteRequest req = new DeporteRequest();
        req.setNombreDeporte(nombre);
        req.setDescripcionDeporte("Desc");

        mockMvc.perform(post("/private/deporte/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/private/deporte/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").exists());
    }
}