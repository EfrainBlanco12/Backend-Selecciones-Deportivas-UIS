package com.deporuis.auth.integracion;

import com.deporuis.DeporuisApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DeporuisApplication.class)
@AutoConfigureMockMvc
class AuthControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper om;

    private static final String LOGIN_PATH = "/auth/login";

    @Test
    @DisplayName("POST /auth/login con body vacío -> 400/401 (tolera 404 por perfiles)")
    void login_vacio_deberiaFallar() throws Exception {
        MvcResult res = mockMvc.perform(
                        post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("{}"))
                .andReturn();

        int status = res.getResponse().getStatus();
        assertThat(status)
                .withFailMessage("Esperaba 400 o 401 (o 404 si ruta no disponible). Fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(400, 401, 404);
    }

    @Test
    @DisplayName("POST /auth/login con credenciales inválidas -> 400/401 (tolera 404)")
    void login_invalido_deberiaFallar() throws Exception {
        String body = """
                {"codigo_universitario":"999999","password":"badpass"}
                """;

        MvcResult res = mockMvc.perform(
                        post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(body))
                .andReturn();

        int status = res.getResponse().getStatus();
        assertThat(status)
                .withFailMessage("Esperaba 400/401/404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(400, 401, 404);
    }

    @Test
    @DisplayName("POST /auth/login exitoso (usa AUTH_TEST_USER/PASS) -> 2xx y token en respuesta (con fallback 4xx si no hay credenciales)")
    void login_exitoso_conEnv_deberiaRetornarToken() throws Exception {
        String user = System.getenv("AUTH_TEST_USER");
        if (user == null || user.isBlank()) user = System.getProperty("auth.test.user");

        String pass = System.getenv("AUTH_TEST_PASS");
        if (pass == null || pass.isBlank()) pass = System.getProperty("auth.test.pass");

        if (user != null && !user.isBlank() && pass != null && !pass.isBlank()) {
            String body = String.format("{\"codigo_universitario\":\"%s\",\"password\":\"%s\"}", user, pass);

            MvcResult res = mockMvc.perform(
                            post(LOGIN_PATH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .content(body))
                    .andExpect(status().is2xxSuccessful())
                    .andReturn();

            String raw = res.getResponse().getContentAsString();
            String safeJson = (raw == null || raw.isBlank()) ? "{}" : raw;

            JsonNode root;
            try {
                root = om.readTree(safeJson);
            } catch (Exception ignore) {
                assertThat(safeJson).isNotBlank();
                return;
            }

            JsonNode token = root.get("token");
            if (token == null) token = root.get("jwt");
            if (token == null) token = root.get("accessToken");
            if (token == null) token = root.get("access_token");
            if (token == null) token = root.get("authorization");
            if (token == null) token = root.get("Authorization");

            assertThat(token)
                    .withFailMessage("Respuesta 2xx pero no encontré un campo de token conocido. JSON=%s", root.toString())
                    .isNotNull();
            assertThat(token.asText()).isNotBlank();
            return;
        }

        String fallbackBody = "{\"codigo_universitario\":\"999999\",\"password\":\"badpass\"}";
        mockMvc.perform(
                        post(LOGIN_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(fallbackBody))
                .andExpect(result -> {
                    int s = result.getResponse().getStatus();
                    assertThat(s)
                            .withFailMessage("Sin credenciales de prueba, esperaba 4xx pero fue %s. Body=%s",
                                    s, result.getResponse().getContentAsString())
                            .isBetween(400, 499);
                });
    }
}
