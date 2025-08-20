package com.deporuis.auth.integracion;

import com.deporuis.DeporuisApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = DeporuisApplication.class)
@AutoConfigureMockMvc
class SecurityAccessIT {

    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("GET /private/deporte/lista sin Authorization -> 401/403")
    void acceso_privado_sinToken_deberiaFallar() throws Exception {
        MvcResult res = mockMvc.perform(
                        get("/private/deporte/lista")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = res.getResponse().getStatus();
        assertThat(status)
                .withFailMessage("Esperaba 401 o 403, pero fue %s. Body=%s", status, res.getResponse().getContentAsString())
                .isIn(401, 403);
    }

    @Test
    @DisplayName("GET /private/deporte/lista con Bearer inválido -> 401/403 y sin Set-Cookie")
    void acceso_privado_conTokenInvalido_deberiaFallar() throws Exception {
        MvcResult res = mockMvc.perform(
                        get("/private/deporte/lista")
                                .header("Authorization", "Bearer INVALIDO")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = res.getResponse().getStatus();
        assertThat(status).isIn(401, 403);
        // esperamos stateless
        String setCookie = res.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).as("No debería establecer cookies en fallo de auth").isNull();
    }
}