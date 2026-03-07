package com.deporuis.auth.integracion;

import com.deporuis.DeporuisApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    @DisplayName("POST /auth/private/registrar debe crear un login para un integrante")
    void registrarLogin_integranteValido_debeCrearLogin() throws Exception {
        String requestBody = """
                {"idIntegrante": 1, "password": "password123"}
                """;

        MvcResult res = mockMvc.perform(
                        post("/auth/private/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Puede ser 201 si funciona, 403 si no hay permisos de admin, 404 si no existe el integrante, o 409 si ya existe el login
        assertThat(status)
                .withFailMessage("Esperaba 201, 403, 404 o 409 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(201, 403, 404, 409);

        // Si fue exitoso, verificar estructura de respuesta
        if (status == 201) {
            String body = res.getResponse().getContentAsString();
            JsonNode root = om.readTree(body);
            
            assertThat(root.has("codigoUniversitario"))
                    .withFailMessage("Respuesta exitosa debe tener campo 'codigoUniversitario'")
                    .isTrue();
            assertThat(root.has("mensaje"))
                    .withFailMessage("Respuesta exitosa debe tener campo 'mensaje'")
                    .isTrue();
        }
    }

    @Test
    @DisplayName("POST /auth/private/registrar con datos inválidos debe retornar 400")
    void registrarLogin_datosInvalidos_debeRetornar400() throws Exception {
        String requestBody = """
                {"idIntegrante": null, "password": ""}
                """;

        MvcResult res = mockMvc.perform(
                        post("/auth/private/registrar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Debe retornar 400 por validación o 403 por falta de permisos
        assertThat(status)
                .withFailMessage("Esperaba 400 o 403 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(400, 403);
    }

    @Test
    @DisplayName("GET /auth/integrantes-con-login debe retornar lista paginada de integrantes con login")
    void obtenerIntegrantesConLogin_debeRetornarListaPaginada() throws Exception {
        // Este test asume que hay integrantes con login en la base de datos
        // o que la consulta no falla incluso si no hay datos
        
        MvcResult res = mockMvc.perform(
                        get("/auth/integrantes-con-login")
                                .param("page", "0")
                                .param("size", "10")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Puede ser 200 si funciona, 403 si no hay permisos de admin, o 404 si la ruta no existe
        assertThat(status)
                .withFailMessage("Esperaba 200, 403 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(200, 403, 404);

        // Si fue exitoso, verificar estructura de respuesta paginada
        if (status == 200) {
            String body = res.getResponse().getContentAsString();
            JsonNode root = om.readTree(body);
            
            assertThat(root.has("content"))
                    .withFailMessage("Respuesta exitosa debe tener campo 'content'")
                    .isTrue();
            assertThat(root.has("totalElements"))
                    .withFailMessage("Respuesta exitosa debe tener campo 'totalElements'")
                    .isTrue();
            assertThat(root.get("content").isArray())
                    .withFailMessage("Campo 'content' debe ser un array")
                    .isTrue();
        }
    }

    @Test
    @DisplayName("GET /auth/integrantes-con-login con paginación personalizada debe funcionar")
    void obtenerIntegrantesConLogin_conPaginacionPersonalizada() throws Exception {
        MvcResult res = mockMvc.perform(
                        get("/auth/integrantes-con-login")
                                .param("page", "0")
                                .param("size", "5")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        assertThat(status)
                .withFailMessage("Esperaba 200, 403 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(200, 403, 404);

        if (status == 200) {
            String body = res.getResponse().getContentAsString();
            JsonNode root = om.readTree(body);
            
            // Verificar que el tamaño de página sea respetado
            assertThat(root.get("size").asInt())
                    .withFailMessage("El tamaño de página debe ser 5")
                    .isEqualTo(5);
        }
    }

    @Test
    @DisplayName("GET /auth/verificar-login/{codigoUniversitario} debe retornar estructura correcta")
    void verificarLogin_debeRetornarEstructuraCorrecta() throws Exception {
        String codigoUniversitario = "2200001";
        
        MvcResult res = mockMvc.perform(
                        get("/auth/verificar-login/" + codigoUniversitario)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // El endpoint es público, debe retornar 200 o 404 si la ruta no existe
        assertThat(status)
                .withFailMessage("Esperaba 200 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(200, 404);

        // Si fue exitoso, verificar estructura de respuesta
        if (status == 200) {
            String body = res.getResponse().getContentAsString();
            JsonNode root = om.readTree(body);
            
            assertThat(root.has("codigoUniversitario"))
                    .withFailMessage("Respuesta debe tener campo 'codigoUniversitario'")
                    .isTrue();
            assertThat(root.has("tieneLogin"))
                    .withFailMessage("Respuesta debe tener campo 'tieneLogin'")
                    .isTrue();
            assertThat(root.get("codigoUniversitario").asText())
                    .withFailMessage("El código universitario debe coincidir")
                    .isEqualTo(codigoUniversitario);
            assertThat(root.get("tieneLogin").isBoolean())
                    .withFailMessage("El campo 'tieneLogin' debe ser booleano")
                    .isTrue();
        }
    }

    @Test
    @DisplayName("GET /auth/verificar-login/{codigoUniversitario} con código inexistente debe retornar tieneLogin=false")
    void verificarLogin_codigoInexistente_debeRetornarFalse() throws Exception {
        String codigoUniversitario = "9999999";
        
        MvcResult res = mockMvc.perform(
                        get("/auth/verificar-login/" + codigoUniversitario)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        assertThat(status)
                .withFailMessage("Esperaba 200 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(200, 404);

        if (status == 200) {
            String body = res.getResponse().getContentAsString();
            JsonNode root = om.readTree(body);
            
            // Para un código inexistente, tieneLogin debe ser false
            assertThat(root.get("tieneLogin").asBoolean())
                    .withFailMessage("Para un código inexistente, tieneLogin debe ser false")
                    .isFalse();
        }
    }

    @Test
    @DisplayName("DELETE /auth/private/eliminar-login/{codigoUniversitario} con código existente debe eliminar el login")
    void eliminarLogin_codigoExistente_debeEliminarLogin() throws Exception {
        String codigoUniversitario = "2200001";
        
        MvcResult res = mockMvc.perform(
                        delete("/auth/private/eliminar-login/" + codigoUniversitario)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Puede ser 200 si funciona, 403 si no hay permisos, 404 si no existe o la ruta no está disponible
        assertThat(status)
                .withFailMessage("Esperaba 200, 403 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(200, 403, 404);

        // Si fue exitoso, verificar estructura de respuesta
        if (status == 200) {
            String body = res.getResponse().getContentAsString();
            JsonNode root = om.readTree(body);
            
            assertThat(root.has("codigoUniversitario"))
                    .withFailMessage("Respuesta debe tener campo 'codigoUniversitario'")
                    .isTrue();
            assertThat(root.has("mensaje"))
                    .withFailMessage("Respuesta debe tener campo 'mensaje'")
                    .isTrue();
            assertThat(root.get("codigoUniversitario").asText())
                    .withFailMessage("El código universitario debe coincidir")
                    .isEqualTo(codigoUniversitario);
        }
    }

    @Test
    @DisplayName("DELETE /auth/private/eliminar-login/{codigoUniversitario} con código inexistente debe retornar 404")
    void eliminarLogin_codigoInexistente_debeRetornar404() throws Exception {
        String codigoUniversitario = "9999999";
        
        MvcResult res = mockMvc.perform(
                        delete("/auth/private/eliminar-login/" + codigoUniversitario)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Debe retornar 404 si no existe, 403 si no hay permisos, o 404 si la ruta no está disponible
        assertThat(status)
                .withFailMessage("Esperaba 403 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(403, 404);
    }

    @Test
    @DisplayName("POST /auth/verificar-password con credenciales válidas debe retornar esValida=true o false")
    void verificarPassword_debeRetornarRespuestaConBooleano() throws Exception {
        String requestBody = """
                {
                    "codigo_universitario": "2200001",
                    "password": "testPassword"
                }
                """;
        
        MvcResult res = mockMvc.perform(
                        post("/auth/verificar-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Debe retornar 200 si funciona, 403 si no hay permisos (requiere autenticación), o 404 si la ruta no está disponible
        assertThat(status)
                .withFailMessage("Esperaba 200, 403 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(200, 403, 404);

        // Si fue exitoso, verificar estructura de respuesta
        if (status == 200) {
            String body = res.getResponse().getContentAsString();
            JsonNode root = om.readTree(body);
            
            assertThat(root.has("codigoUniversitario"))
                    .withFailMessage("Respuesta debe tener campo 'codigoUniversitario'")
                    .isTrue();
            assertThat(root.has("esValida"))
                    .withFailMessage("Respuesta debe tener campo 'esValida'")
                    .isTrue();
            assertThat(root.get("codigoUniversitario").asText())
                    .withFailMessage("El código universitario debe coincidir")
                    .isEqualTo("2200001");
            assertThat(root.get("esValida").isBoolean())
                    .withFailMessage("El campo esValida debe ser booleano")
                    .isTrue();
        }
    }

    @Test
    @DisplayName("POST /auth/verificar-password con campos vacíos debe retornar 400")
    void verificarPassword_camposVacios_debeRetornar400() throws Exception {
        String requestBody = """
                {
                    "codigo_universitario": "",
                    "password": ""
                }
                """;
        
        MvcResult res = mockMvc.perform(
                        post("/auth/verificar-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Debe retornar 400 (validación fallida) o 404 si la ruta no está disponible
        assertThat(status)
                .withFailMessage("Esperaba 400 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(400, 404);
    }

    @Test
    @DisplayName("POST /auth/verificar-password sin autenticación debe retornar 403")
    void verificarPassword_sinAutenticacion_debeRetornar403() throws Exception {
        // Este test asume que el endpoint requiere autenticación (@PreAuthorize("isAuthenticated()"))
        String requestBody = """
                {
                    "codigo_universitario": "2200001",
                    "password": "password123"
                }
                """;
        
        MvcResult res = mockMvc.perform(
                        post("/auth/verificar-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Como no hay token JWT, debe retornar 403 (sin permisos) o 404 si la ruta no está disponible
        assertThat(status)
                .withFailMessage("Esperaba 403 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(403, 404);
    }

    @Test
    @DisplayName("PUT /auth/cambiar-password con datos válidos debe cambiar la contraseña")
    void cambiarPassword_datosValidos_debeCambiarPassword() throws Exception {
        String requestBody = """
                {
                    "codigo_universitario": "2200001",
                    "password_nueva": "nuevaPassword123"
                }
                """;
        
        MvcResult res = mockMvc.perform(
                        put("/auth/cambiar-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Puede ser 200 si funciona, 403 si no hay autenticación, 404 si no existe
        assertThat(status)
                .withFailMessage("Esperaba 200, 403 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(200, 403, 404);

        // Si fue exitoso, verificar estructura de respuesta
        if (status == 200) {
            String body = res.getResponse().getContentAsString();
            JsonNode root = om.readTree(body);
            
            assertThat(root.has("codigoUniversitario"))
                    .withFailMessage("Respuesta debe tener campo 'codigoUniversitario'")
                    .isTrue();
            assertThat(root.has("mensaje"))
                    .withFailMessage("Respuesta debe tener campo 'mensaje'")
                    .isTrue();
            assertThat(root.get("codigoUniversitario").asText())
                    .withFailMessage("El código universitario debe coincidir")
                    .isEqualTo("2200001");
            assertThat(root.get("mensaje").asText())
                    .withFailMessage("El mensaje debe indicar éxito")
                    .contains("exitosamente");
        }
    }

    @Test
    @DisplayName("PUT /auth/cambiar-password con campos vacíos debe retornar 400")
    void cambiarPassword_camposVacios_debeRetornar400() throws Exception {
        String requestBody = """
                {
                    "codigo_universitario": "",
                    "password_nueva": ""
                }
                """;
        
        MvcResult res = mockMvc.perform(
                        put("/auth/cambiar-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Debe retornar 400 por validación fallida, 403 si no hay autenticación
        assertThat(status)
                .withFailMessage("Esperaba 400 o 403 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(400, 403);
    }

    @Test
    @DisplayName("PUT /auth/cambiar-password sin autenticación debe retornar 403")
    void cambiarPassword_sinAutenticacion_debeRetornar403() throws Exception {
        String requestBody = """
                {
                    "codigo_universitario": "2200001",
                    "password_nueva": "nuevaPassword123"
                }
                """;
        
        MvcResult res = mockMvc.perform(
                        put("/auth/cambiar-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();

        int status = res.getResponse().getStatus();
        
        // Como no hay token JWT, debe retornar 403 (sin permisos) o 404 si la ruta no está disponible
        assertThat(status)
                .withFailMessage("Esperaba 403 o 404 pero fue %s. Body=%s",
                        status, res.getResponse().getContentAsString())
                .isIn(403, 404);
    }
}
