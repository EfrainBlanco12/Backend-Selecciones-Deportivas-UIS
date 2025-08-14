package com.deporuis.logro.integracion;

import com.deporuis.logro.aplicacion.LogroService;
import com.deporuis.logro.infraestructura.LogroController;
import com.deporuis.logro.infraestructura.dto.LogroRequest;
import com.deporuis.logro.infraestructura.dto.LogroResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@TestPropertySource(properties = "spring.profiles.active=test")
@WebMvcTest(
        controllers = LogroController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.deporuis\\.auth\\..*"
        )
)
@AutoConfigureMockMvc(addFilters = true)
class LogroControllerIT {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockBean private LogroService service;
    @MockBean private com.deporuis.auth.aplicacion.JwtService jwtService;
    @MockBean private com.deporuis.auth.infraestructura.JwtFilter jwtFilter;

    private LogroRequest buildCrear() {
        LogroRequest r = new LogroRequest();
        r.setFechaLogro(LocalDate.of(2024, 5, 20));
        r.setNombreLogro("Título Regional");
        r.setDescripcionLogro("Torneo Apertura");
        r.setSelecciones(List.of(1, 2));
        return r;
    }

    private LogroResponse buildResponse() {
        return new LogroResponse(10, LocalDate.of(2024, 5, 20), "Título Regional", "Torneo Apertura", List.of(1, 2));
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void crear_retorna200() throws Exception {
        when(service.crearLogro(any())).thenReturn(buildResponse());

        mvc.perform(post("/private/logro/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildCrear())))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void obtenerPorId_retorna200() throws Exception {
        when(service.obtenerLogro(5)).thenReturn(buildResponse());

        mvc.perform(get("/private/logro/obtener/5"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void lista_retorna200() throws Exception {
        Page<LogroResponse> page = new PageImpl<>(List.of(buildResponse()));
        when(service.obtenerLogrosPaginados(0, 5)).thenReturn(page);

        mvc.perform(get("/private/logro/lista").param("page", "0").param("size", "5"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void actualizar_retorna200() throws Exception {
        when(service.actualizarLogro(eq(3), any())).thenReturn(buildResponse());

        mvc.perform(put("/private/logro/actualizar/3")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildCrear())))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void eliminar_retorna200() throws Exception {
        mvc.perform(delete("/private/logro/eliminar/9").with(csrf()))
                .andExpect(status().isOk());
    }
}
