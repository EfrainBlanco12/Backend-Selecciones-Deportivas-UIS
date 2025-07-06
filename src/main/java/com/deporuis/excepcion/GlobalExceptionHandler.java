package com.deporuis.excepcion;

import com.deporuis.auth.excepciones.AccesoDenegadoCreacionIntegrantesException;
import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
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

    // Valida si los datos vienen con el formato correcto
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<?> handleInvalidFormatException(InvalidFormatException ex) {
        Map<String, String> error = new HashMap<>();

        if (ex.getTargetType().isEnum()) {
            Object[] valores = ex.getTargetType().getEnumConstants();
            error.put("error", "Valor inválido para el campo enum.");
            error.put("valores_permitidos", Arrays.toString(valores));
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        // Otros casos
        error.put("error", "Formato inválido en el JSON.");
        error.put("detalle", ex.getOriginalMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccesoDenegadoCreacionIntegrantesException.class)
    public ResponseEntity<ErrorResponse> manejarAccesoDenegado(AccesoDenegadoCreacionIntegrantesException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                System.currentTimeMillis()
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    // DTO interno para estandarizar el cuerpo de error
    public record ErrorResponse(int status, String mensaje, long timestamp) {}
}

