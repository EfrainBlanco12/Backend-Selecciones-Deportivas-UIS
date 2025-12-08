package com.deporuis.seleccion.integracion;

import com.deporuis.seleccion.aplicacion.SeleccionService;
import com.deporuis.seleccion.dominio.TipoSeleccion;
import com.deporuis.seleccion.infraestructura.SeleccionController;
import com.deporuis.seleccion.infraestructura.dto.SeleccionPatchRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionRequest;
import com.deporuis.seleccion.infraestructura.dto.SeleccionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@TestPropertySource(properties = "spring.profiles.active=test")

@WebMvcTest(controllers = SeleccionController.class)
@AutoConfigureMockMvc(addFilters = false) // deshabilitar filtros para testing
class SeleccionControllerIT {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockitoBean private SeleccionService service;
    @MockitoBean private com.deporuis.auth.infraestructura.JwtFilter jwtFilter;

    private SeleccionRequest buildReq() {
        SeleccionRequest r = new SeleccionRequest();
        r.setFechaCreacion(LocalDate.of(2025, 1, 1));
        r.setNombreSeleccion("Seleccion UIS");
        r.setEspacioDeportivo("Coliseo UIS");
        r.setEquipo(true);
        r.setTipo_seleccion(TipoSeleccion.MIXTO);
        r.setIdDeporte(1);
        r.setFotos(List.of());
        r.setHorarios(List.of());
        return r;
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void crear_retorna200_ok() throws Exception {
        SeleccionResponse resp = new SeleccionResponse();
        resp.setIdSeleccion(1);
        resp.setNombreSeleccion("Seleccion UIS");
        resp.setUsuarioModifico(100);

        when(service.crearSeleccion(any(), eq(100))).thenReturn(resp);

        mvc.perform(post("/private/seleccion/crear")
                        .with(csrf()) // CSRF requerido
                        .header("usuariomodifico", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildReq())))
                .andExpect(status().isCreated()) // tu controller devuelve 201 CREATED
                .andExpect(jsonPath("$.usuarioModifico").value(100));
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void lista_retorna200() throws Exception {
        SeleccionResponse item = new SeleccionResponse();
        item.setIdSeleccion(2);
        item.setNombreSeleccion("A");

        when(service.obtenerSeleccionesPaginadas(0, 5))
                .thenReturn(new PageImpl<>(List.of(item)));

        mvc.perform(get("/private/seleccion/lista")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void obtenerPorId_retorna200() throws Exception {
        SeleccionResponse resp = new SeleccionResponse();
        resp.setIdSeleccion(9);
        resp.setNombreSeleccion("B");

        when(service.obtenerSeleccion(9)).thenReturn(resp);

        mvc.perform(get("/private/seleccion/obtener/9"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void actualizar_retorna200() throws Exception {
        SeleccionResponse resp = new SeleccionResponse();
        resp.setIdSeleccion(3);
        resp.setNombreSeleccion("C");
        resp.setUsuarioModifico(200);

        when(service.actualizarSeleccion(eq(3), any(), eq(200)))
                .thenReturn(resp);

        mvc.perform(put("/private/seleccion/actualizar/3")
                        .with(csrf()) // CSRF requerido
                        .header("usuariomodifico", "200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildReq())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioModifico").value(200));
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void eliminar_retorna200_ok() throws Exception {
        mvc.perform(delete("/private/seleccion/eliminar/11")
                        .with(csrf())) // CSRF requerido
                .andExpect(status().isNoContent()); // tu controller devuelve 204 NO CONTENT
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void softdelete_retorna200_ok() throws Exception {
        mvc.perform(patch("/private/seleccion/softdelete/12")
                        .with(csrf())) // CSRF requerido
                .andExpect(status().isNoContent()); // tu controller devuelve 204 NO CONTENT
    }

    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void actualizarParcial_soloNombre_retorna200() throws Exception {
        SeleccionPatchRequest patchReq = new SeleccionPatchRequest();
        patchReq.setNombreSeleccion("Nombre Actualizado");

        SeleccionResponse resp = new SeleccionResponse();
        resp.setIdSeleccion(15);
        resp.setNombreSeleccion("Nombre Actualizado");
        resp.setUsuarioModifico(300);

        when(service.actualizarSeleccionParcial(eq(15), any(), eq(300)))
                .thenReturn(resp);

        mvc.perform(patch("/private/seleccion/actualizar-parcial/15")
                        .with(csrf())
                        .header("usuariomodifico", "300")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(patchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreSeleccion").value("Nombre Actualizado"))
                .andExpect(jsonPath("$.usuarioModifico").value(300));
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void actualizarParcial_soloCamposBasicos_retorna200() throws Exception {
        SeleccionPatchRequest patchReq = new SeleccionPatchRequest();
        patchReq.setNombreSeleccion("Nuevo nombre");
        patchReq.setEspacioDeportivo("Nueva cancha");
        patchReq.setEquipo(false);

        SeleccionResponse resp = new SeleccionResponse();
        resp.setIdSeleccion(20);
        resp.setNombreSeleccion("Nuevo nombre");
        resp.setUsuarioModifico(400);

        when(service.actualizarSeleccionParcial(eq(20), any(), eq(400)))
                .thenReturn(resp);

        mvc.perform(patch("/private/seleccion/actualizar-parcial/20")
                        .with(csrf())
                        .header("usuariomodifico", "400")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(patchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioModifico").value(400));
    }

    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void actualizarParcial_sinDatos_retorna200() throws Exception {
        // Enviar un request vacío, debería devolver la selección sin cambios
        SeleccionPatchRequest patchReq = new SeleccionPatchRequest();

        SeleccionResponse resp = new SeleccionResponse();
        resp.setIdSeleccion(25);
        resp.setNombreSeleccion("Sin cambios");
        resp.setUsuarioModifico(500);

        when(service.actualizarSeleccionParcial(eq(25), any(), eq(500)))
                .thenReturn(resp);

        mvc.perform(patch("/private/seleccion/actualizar-parcial/25")
                        .with(csrf())
                        .header("usuariomodifico", "500")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(patchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreSeleccion").value("Sin cambios"))
                .andExpect(jsonPath("$.usuarioModifico").value(500));
    }
}
