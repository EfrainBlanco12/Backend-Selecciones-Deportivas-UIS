package com.deporuis.excepcion;

import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // valida campos nulos o vacios
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Error de validación");

        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Valida deporte existente
    @ExceptionHandler(DeporteYaExisteException.class)
    public ResponseEntity<Map<String, String>> handleDeporteExistente(DeporteYaExisteException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Valida datos de autenticacion
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<?> handleInternalAuthError(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error en autenticación: " + ex.getMessage());
    }

    // Valida si una entidad existe
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleNotFound(ResourceNotFoundException ex) {
        Map<String,String> body = Map.of("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Valida si se pasa la informacion completa en los endpoint
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String,String>> handleBadRequest(BadRequestException ex) {
        Map<String,String> body = Map.of("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}

