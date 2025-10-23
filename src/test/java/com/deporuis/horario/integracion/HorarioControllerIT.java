package com.deporuis.horario.integracion;

import com.deporuis.horario.aplicacion.HorarioService;
import com.deporuis.horario.infraestructura.HorarioController;
import com.deporuis.horario.infraestructura.dto.HorarioRequest;
import com.deporuis.horario.infraestructura.dto.HorarioResponse;
import com.deporuis.horario.dominio.DiaHorario;
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

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@TestPropertySource(properties = "spring.profiles.active=test")
@WebMvcTest(
        controllers = HorarioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.deporuis\\.auth\\..*"
        )
)
@AutoConfigureMockMvc(addFilters = true)
class HorarioControllerIT {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockBean private HorarioService service;
    @MockBean private com.deporuis.auth.aplicacion.JwtService jwtService;
    @MockBean private com.deporuis.auth.infraestructura.JwtFilter jwtFilter;

    private HorarioRequest buildCrear() {
        HorarioRequest r = new HorarioRequest();
        r.setDia(DiaHorario.LUNES);
        r.setHoraInicio(LocalTime.of(8,0));
        r.setHoraFin(LocalTime.of(10,0));
        return r;
    }

    private HorarioResponse buildResponse() {
        return new HorarioResponse(10, DiaHorario.LUNES, LocalTime.of(8,0), LocalTime.of(10,0), List.of());
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void crear_retorna200() throws Exception {
        when(service.crearHorario(any())).thenReturn(buildResponse());

        mvc.perform(post("/private/horario/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildCrear())))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void obtenerPorId_retorna200() throws Exception {
        when(service.obtenerHorario(5)).thenReturn(buildResponse());

        mvc.perform(get("/private/horario/obtener/5"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void lista_retorna200() throws Exception {
        Page<HorarioResponse> page = new PageImpl<>(List.of(buildResponse()));
        when(service.obtenerHorariosPaginados(0, 5)).thenReturn(page);

        mvc.perform(get("/private/horario/lista").param("page", "0").param("size", "5"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void crear_conSelecciones_retorna200() throws Exception {
        HorarioRequest request = buildCrear();
        request.setIdSelecciones(List.of(1, 2, 3));

        HorarioResponse response = buildResponse();
        when(service.crearHorario(any())).thenReturn(response);

        mvc.perform(post("/private/horario/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void crear_conUnaSeleccion_retorna200() throws Exception {
        HorarioRequest request = buildCrear();
        request.setIdSelecciones(List.of(5));

        HorarioResponse response = buildResponse();
        when(service.crearHorario(any())).thenReturn(response);

        mvc.perform(post("/private/horario/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void actualizar_retorna200() throws Exception {
        HorarioRequest request = buildCrear();
        request.setDia(DiaHorario.VIERNES);

        HorarioResponse response = buildResponse();
        when(service.actualizarHorario(eq(10), any())).thenReturn(response);

        mvc.perform(put("/private/horario/actualizar/10")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void actualizar_conSelecciones_retorna200() throws Exception {
        HorarioRequest request = buildCrear();
        request.setIdSelecciones(List.of(1, 2, 3));

        HorarioResponse response = buildResponse();
        when(service.actualizarHorario(eq(15), any())).thenReturn(response);

        mvc.perform(put("/private/horario/actualizar/15")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void eliminar_retorna204() throws Exception {
        mvc.perform(delete("/private/horario/eliminar/10")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void eliminar_sinRolAdministrador_retorna403() throws Exception {
        mvc.perform(delete("/private/horario/eliminar/10")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}