package com.deporuis.seleccion.integracion;

import com.deporuis.seleccion.dominio.TipoSeleccion;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import com.deporuis.seleccion.aplicacion.SeleccionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SeleccionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SeleccionService seleccionService;

    private SeleccionRequest crearRequestValido() {
        SeleccionRequest request = new SeleccionRequest();
        request.setNombreSeleccion("Fútbol");
        request.setEspacioDeportivo("Cancha");
        request.setEquipo(Boolean.valueOf("A"));
        request.setFechaCreacion(LocalDate.now());
        request.setTipo_seleccion(TipoSeleccion.MIXTO);
        request.setIdDeporte(1);
        request.setFotos(Collections.emptyList());
        request.setHorarios(Collections.emptyList());
        return request;
    }


    // Crear (requiere ADMINISTRADOR o ENTRENADOR)
    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void crearSeleccion_deberiaRetornar201() throws Exception {
        when(seleccionService.crearSeleccion(any())).thenReturn(new SeleccionResponse());

        mockMvc.perform(post("/private/seleccion/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequestValido())))
                .andExpect(status().isCreated());
    }

    // Obtener lista (público)
    @Test
    void obtenerSeleccionesPaginadas_deberiaRetornar200() throws Exception {
        when(seleccionService.obtenerSeleccionesPaginadas(anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(Collections.singletonList(new SeleccionResponse()), PageRequest.of(0, 3), 1));

        mockMvc.perform(get("/private/seleccion/lista?page=0&size=3"))
                .andExpect(status().isOk());
    }

    // Obtener por ID (público)
    @Test
    void obtenerSeleccion_deberiaRetornar200SiExiste() throws Exception {
        when(seleccionService.obtenerSeleccion(1)).thenReturn(new SeleccionResponse());

        mockMvc.perform(get("/private/seleccion/obtener/1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerSeleccion_deberiaRetornar404SiNoExiste() throws Exception {
        when(seleccionService.obtenerSeleccion(99)).thenReturn(null);

        mockMvc.perform(get("/private/seleccion/obtener/99"))
                .andExpect(status().isNotFound());
    }

    // Eliminar (requiere ADMINISTRADOR)
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void eliminarSeleccion_deberiaRetornar204() throws Exception {
        mockMvc.perform(delete("/private/seleccion/eliminar/1"))
                .andExpect(status().isNoContent());

        verify(seleccionService).eliminarSeleccion(1);
    }

    // Soft delete (ADMINISTRADOR o ENTRENADOR)
    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void softDeleteSeleccion_deberiaRetornar204() throws Exception {
        mockMvc.perform(patch("/private/seleccion/softdelete/1"))
                .andExpect(status().isNoContent());

        verify(seleccionService).softDeleteSeleccion(1);
    }

    // Actualizar (ADMINISTRADOR o ENTRENADOR)
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void actualizarSeleccion_deberiaRetornar200() throws Exception {
        when(seleccionService.actualizarSeleccion(eq(1), any())).thenReturn(new SeleccionResponse());

        mockMvc.perform(put("/private/seleccion/actualizar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequestValido())))
                .andExpect(status().isOk());
    }
}
