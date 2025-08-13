package com.deporuis.posicion.integracion;

import com.deporuis.deporte.dominio.Deporte;
import com.deporuis.posicion.aplicacion.PosicionService;
import com.deporuis.posicion.dominio.Posicion;
import com.deporuis.posicion.infraestructura.PosicionController;
import com.deporuis.posicion.infraestructura.dto.PosicionActualizarRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionRequest;
import com.deporuis.posicion.infraestructura.dto.PosicionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@TestPropertySource(properties = "spring.profiles.active=test")
@WebMvcTest(
        controllers = PosicionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.deporuis\\.auth\\..*"
        )
)
@AutoConfigureMockMvc(addFilters = true)
class PosicionControllerIT {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockBean private PosicionService service;

    @MockBean private com.deporuis.auth.aplicacion.JwtService jwtService;
    @MockBean private com.deporuis.auth.infraestructura.JwtFilter jwtFilter;

    private PosicionRequest buildCrear() {
        PosicionRequest r = new PosicionRequest();
        r.setNombrePosicion("Central");
        r.setIdDeporte(1);
        return r;
    }

    private PosicionActualizarRequest buildActualizar() {
        PosicionActualizarRequest r = new PosicionActualizarRequest();
        r.setNombrePosicion("Central Ofensivo");
        return r;
    }

    private PosicionResponse buildResponse() {
        Deporte dep = new Deporte();
        dep.setIdDeporte(1);
        dep.setNombreDeporte("Fútbol");
        Posicion pos = new Posicion();
        pos.setIdPosicion(99);
        pos.setNombrePosicion("Central");
        pos.setDeporte(dep);
        pos.setVisibilidad(true);
        return new PosicionResponse(pos);
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void crear_retorna200_ok() throws Exception {
        when(service.crearPosicion(any())).thenReturn(buildResponse());

        mvc.perform(post("/private/posicion/crear")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildCrear())))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void listarPorDeporte_retorna200() throws Exception {
        when(service.obtenerPosicionPorDeporte(2)).thenReturn(List.of(buildResponse()));

        mvc.perform(get("/private/posicion/por-deporte/2"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void actualizar_retorna200() throws Exception {
        when(service.actualizarPosicion(eq(3), any())).thenReturn(buildResponse());

        mvc.perform(put("/private/posicion/actualizar/3")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildActualizar())))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void softdelete_retorna200_ok() throws Exception {
        when(service.softDelete(7)).thenReturn(buildResponse());

        mvc.perform(put("/private/posicion/softdelete/7")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
