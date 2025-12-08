package com.deporuis.publicacion.integracion;

import com.deporuis.publicacion.aplicacion.PublicacionService;
import com.deporuis.publicacion.infraestructura.PublicacionController;
import com.deporuis.publicacion.infraestructura.dto.PublicacionRequest;
import com.deporuis.publicacion.infraestructura.dto.PublicacionResponse;
import com.deporuis.publicacion.dominio.TipoPublicacion;
import com.deporuis.Foto.infraestructura.dto.FotoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Nota:
 * - Desactivamos filtros de seguridad en este slice para evitar 401/403.
 * - Simulamos un usuario con rol ADMINISTRADOR para todas las pruebas.
 * - Si tu PublicacionController sigue con @Controller (no @RestController),
 *   el "accept(JSON)" ayuda a negociar JSON en vez de resolver vista.
 */
@WebMvcTest(controllers = PublicacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMINISTRADOR")
class PublicacionControllerIT {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockitoBean private PublicacionService publicacionService;
    @MockitoBean private com.deporuis.auth.infraestructura.JwtFilter jwtFilter;
    @MockitoBean private com.deporuis.auth.aplicacion.JwtService jwtService;

    private PublicacionResponse sample() {
        var r = new PublicacionResponse();
        r.setIdPublicacion(1);
        r.setTitulo("Inicio");
        r.setDescripcion("Desc");
        r.setLugar("Cancha A");
        r.setFecha(LocalDateTime.of(2024,1,1,9,0));
        r.setDuracion("2h");
        r.setVisibilidad(true);
        r.setTipoPublicacion(TipoPublicacion.NOTICIA.name()); // en tu DTO es String
        r.setFechaCreacion(LocalDateTime.of(2024,1,1,9,0));
        r.setUsuarioModifico(100);
        r.setFechaModificacion(LocalDateTime.of(2024,1,1,9,0));
        
        var sel1 = new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(10, "Selección 1");
        var sel2 = new com.deporuis.seleccion.infraestructura.dto.SeleccionPublicacionResponse(20, "Selección 2");
        r.setIdSelecciones(List.of(sel1, sel2));
        return r;
    }

    private PublicacionRequest req() {
        var fr = new FotoRequest();
        fr.setContenido(new byte[]{1});
        fr.setTemporada(2024);

        var rq = new PublicacionRequest();
        rq.setTitulo("Inicio");
        rq.setDescripcion("Desc");
        rq.setLugar("Cancha A");
        rq.setFecha(LocalDateTime.of(2024,1,1,9,0));
        rq.setDuracion("2h");
        rq.setTipoPublicacion(TipoPublicacion.NOTICIA);
        rq.setSelecciones(List.of(10,20));
        rq.setFotos(List.of(fr));
        return rq;
    }

    @Test
    void post_crear_deberiaRetornar201ConBody() throws Exception {
        when(publicacionService.crearPublicacion(any(PublicacionRequest.class), eq(100))).thenReturn(sample());

        mvc.perform(post("/private/publicacion/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("usuariomodifico", "100")
                        .content(om.writeValueAsString(req())))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idPublicacion", is(1)))
                .andExpect(jsonPath("$.usuarioModifico", is(100)))
                .andExpect(jsonPath("$.idSelecciones", containsInAnyOrder(10,20)));
    }

    @Test
    void get_lista_paginada_deberiaRetornarPageJson() throws Exception {
        when(publicacionService.obtenerPublicacionesPaginadas(eq(0), eq(5)))
                .thenReturn(new PageImpl<>(List.of(sample()), PageRequest.of(0,5), 1));

        mvc.perform(get("/private/publicacion/lista")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].idPublicacion", is(1)))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    void get_lista_noticias_y_eventos_debenRetornarPageJson() throws Exception {
        when(publicacionService.obtenerNoticiasPaginadas(eq(0), eq(5)))
                .thenReturn(new PageImpl<>(List.of(sample()), PageRequest.of(0,5), 1));
        when(publicacionService.obtenerEventosPaginados(eq(0), eq(5)))
                .thenReturn(new PageImpl<>(List.of(sample()), PageRequest.of(0,5), 1));

        mvc.perform(get("/private/publicacion/lista/noticias")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].tipoPublicacion", is("NOTICIA")));

        mvc.perform(get("/private/publicacion/lista/eventos")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].idPublicacion", is(1)));
    }

    @Test
    void get_obtener_por_id_deberiaRetornar200ConBody() throws Exception {
        when(publicacionService.obtenerPublicacion(1)).thenReturn(sample());

        mvc.perform(get("/private/publicacion/{idPublicacion}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idPublicacion", is(1)))
                .andExpect(jsonPath("$.usuarioModifico", is(100)))
                .andExpect(jsonPath("$.idSelecciones", containsInAnyOrder(10,20)));
    }

    @Test
    void put_actualizar_deberiaRetornar200ConBody() throws Exception {
        when(publicacionService.actualizarPublicacion(eq(1), any(PublicacionRequest.class), eq(200))).thenReturn(sample());

        mvc.perform(put("/private/publicacion/actualizar/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("usuariomodifico", "200")
                        .content(om.writeValueAsString(req())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idPublicacion", is(1)))
                .andExpect(jsonPath("$.usuarioModifico", is(100)));
    }

    @Test
    void patch_softdelete_deberiaRetornar204() throws Exception {
        mvc.perform(patch("/private/publicacion/softdelete/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_eliminar_deberiaRetornar204() throws Exception {
        mvc.perform(delete("/private/publicacion/eliminar/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
