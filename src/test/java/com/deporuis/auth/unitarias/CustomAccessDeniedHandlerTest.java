package com.deporuis.auth.unitarias;

import com.deporuis.auth.infraestructura.CustomAccessDeniedHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CustomAccessDeniedHandlerTest {

    @Test
    void handle_writes_403_and_json() throws IOException, ServletException {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
        HttpServletRequest request = mock(HttpServletRequest.class);
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("x"));

        assertEquals(403, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertEquals("{\"error\": \"No tienes acceso a esta ruta\"}", response.getContentAsString());
    }
}
