package com.deporuis.integrante.integracion;

import com.deporuis.integrante.aplicacion.IntegranteService;
import com.deporuis.integrante.infraestructura.IntegranteController;
import com.deporuis.integrante.infraestructura.dto.IntegranteRequest;
import com.deporuis.integrante.infraestructura.dto.IntegranteResponse;
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
        controllers = IntegranteController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.deporuis\\.auth\\..*"
        )
)
@AutoConfigureMockMvc(addFilters = true)
class IntegranteControllerIT {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockBean private IntegranteService service;
    @MockBean private com.deporuis.auth.aplicacion.JwtService jwtService;
    @MockBean private com.deporuis.auth.infraestructura.JwtFilter jwtFilter;

    private IntegranteRequest buildCrear() {
        IntegranteRequest r = new IntegranteRequest();
        r.setCodigoUniversitario("123");
        r.setNombres("Ana");
        r.setApellidos("Gomez");
        r.setFechaNacimiento(LocalDate.of(2000,1,1));
        r.setAltura(1.7f);
        r.setPeso(60f);
        r.setDorsal(10);
        r.setCorreoInstitucional("ana@correo.uis.edu.co");
        r.setIdRol(3);
        r.setIdSeleccion(1);
        return r;
    }

    private IntegranteResponse buildResponse() {
        return new IntegranteResponse(
                10,
                "123",
                "Ana",
                "Gomez",
                LocalDate.of(2000,1,1),
                1.7f,
                60f,
                10,
                "ana@correo.uis.edu.co",
                3,
                1,
                7,
                List.of(1,2)
        );
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void crear_retorna200() throws Exception {
        when(service.crearIntegrante(any())).thenReturn(buildResponse());

        mvc.perform(post("/private/integrante/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildCrear())))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void obtenerPorId_retorna200() throws Exception {
        when(service.obtenerIntegrante(5)).thenReturn(buildResponse());

        mvc.perform(get("/private/integrante/obtener/5"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void lista_retorna200() throws Exception {
        Page<IntegranteResponse> page = new PageImpl<>(List.of(buildResponse()));
        when(service.obtenerIntegrantesPaginados(0, 5)).thenReturn(page);

        mvc.perform(get("/private/integrante/lista").param("page", "0").param("size", "5"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void actualizar_retorna200() throws Exception {
        when(service.actualizarIntegrante(eq(3), any())).thenReturn(buildResponse());

        mvc.perform(put("/private/integrante/actualizar/3")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildCrear())))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void softdelete_retorna200() throws Exception {
        mvc.perform(patch("/private/integrante/softdelete/7").with(csrf()))
                .andExpect(status().isOk());
    }
}
