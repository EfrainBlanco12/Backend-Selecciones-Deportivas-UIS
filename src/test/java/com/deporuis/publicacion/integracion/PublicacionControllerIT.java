package com.deporuis.publicacion.integracion;

import com.deporuis.publicacion.aplicacion.PublicacionService;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.publicacion.infraestructura.PublicacionController;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Fuerza perfil test y evita que entre "dev"
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.profiles.active=test")

@WebMvcTest(
        controllers = PublicacionController.class,
        // Excluye cualquier bean de auth (JwtFilter, configs, etc.) del slice MVC
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.deporuis\\.auth\\..*"
        )
)
@AutoConfigureMockMvc(addFilters = true) // mantenemos la cadena de seguridad
class PublicacionControllerIT {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockBean private PublicacionService service;

    // Por si algún bean de auth se cuela por config
    @MockBean private com.deporuis.auth.aplicacion.JwtService jwtService;
    @MockBean private com.deporuis.auth.infraestructura.JwtFilter jwtFilter;

    private PublicacionRequest buildReq() {
        PublicacionRequest r = new PublicacionRequest();
        r.setTitulo("T");
        r.setDescripcion("D");
        r.setLugar("L");
        r.setFecha(LocalDateTime.of(2025, 4, 4, 15, 0));
        r.setDuracion("45m");
        r.setTipoPublicacion(TipoPublicacion.NOTICIA);
        r.setSelecciones(List.of(1, 2));
        r.setFotos(List.of());
        return r;
    }

    // POST /crear (protegido) -> requiere rol válido + CSRF
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void crear_retorna200_ok() throws Exception {
        when(service.crearPublicacion(any())).thenReturn(
                new PublicacionResponse(1, "T", "D", "L", LocalDateTime.now(), "45m", TipoPublicacion.NOTICIA, List.of(), List.of())
        );

        mvc.perform(post("/private/publicacion/crear")
                        .with(csrf()) // CSRF requerido
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildReq())))
                .andExpect(status().isOk()); // <- tu controller devuelve 200
    }

    // GET /lista (bajo /private: autenticamos)
    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void lista_retorna200() throws Exception {
        when(service.obtenerPublicacionesPaginadas(0, 5))
                .thenReturn(new PageImpl<>(List.of(
                        new PublicacionResponse(1, "T", "D", "L", LocalDateTime.now(), "45m", TipoPublicacion.NOTICIA, List.of(), List.of())
                )));

        mvc.perform(get("/private/publicacion/lista")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    // GET /obtener/{id} (bajo /private: autenticamos)
    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void obtenerPorId_retorna200() throws Exception {
        when(service.obtenerPublicacion(9)).thenReturn(
                new PublicacionResponse(9, "T", "D", "L", LocalDateTime.now(), "45m", TipoPublicacion.EVENTO, List.of(), List.of())
        );

        mvc.perform(get("/private/publicacion/obtener/9"))
                .andExpect(status().isOk());
    }

    // PUT /actualizar/{id} (protegido) -> requiere rol + CSRF
    @WithMockUser(roles = {"ENTRENADOR"})
    @Test
    void actualizar_retorna200() throws Exception {
        when(service.actualizarPublicacion(eq(3), any()))
                .thenReturn(new PublicacionResponse(3, "T2", "D2", "L2", LocalDateTime.now(), "30m", TipoPublicacion.NOTICIA, List.of(), List.of()));

        mvc.perform(put("/private/publicacion/actualizar/3")
                        .with(csrf()) // CSRF requerido
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(buildReq())))
                .andExpect(status().isOk());
    }

    // DELETE /eliminar/{id} (protegido) -> requiere rol + CSRF
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void eliminar_retorna200_ok() throws Exception {
        mvc.perform(delete("/private/publicacion/eliminar/11")
                        .with(csrf())) // CSRF requerido
                .andExpect(status().isOk()); // <- tu controller devuelve 200
    }

    // PATCH /softdelete/{id} (protegido) -> requiere rol + CSRF
    @WithMockUser(roles = {"ADMINISTRADOR"})
    @Test
    void softdelete_retorna200_ok() throws Exception {
        mvc.perform(patch("/private/publicacion/softdelete/12")
                        .with(csrf())) // CSRF requerido
                .andExpect(status().isOk()); // <- tu controller devuelve 200
    }
}
