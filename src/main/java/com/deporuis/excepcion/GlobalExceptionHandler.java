package com.deporuis.excepcion;

import com.deporuis.auth.excepciones.AccesoDenegadoCreacionIntegrantesException;
import com.deporuis.auth.excepciones.LoginYaExisteException;
import com.deporuis.auth.excepciones.MismaPasswordException;
import com.deporuis.deporte.excepciones.DeporteYaExisteException;
import com.deporuis.excepcion.common.BadRequestException;
import com.deporuis.excepcion.common.ResourceNotFoundException;
import com.deporuis.Foto.excepciones.FotoEnUsoException;
import com.deporuis.integrante.excepciones.IntegranteEntrenadorSeleccionExists;
import com.deporuis.posicion.excepciones.PosicionYaExisteException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(PosicionYaExisteException.class)
    public ResponseEntity<?> handlePosicionYaExiste(PosicionYaExisteException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IntegranteEntrenadorSeleccionExists.class)
    public ResponseEntity<Map<String, Object>> handleEntrenadorSeleccionExists(IntegranteEntrenadorSeleccionExists ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());// 409 Conflict es semánticamente correcto
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(FotoEnUsoException.class)
    public ResponseEntity<Map<String, String>> handleFotoEnUso(FotoEnUsoException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(LoginYaExisteException.class)
    public ResponseEntity<Map<String, String>> handleLoginYaExiste(LoginYaExisteException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MismaPasswordException.class)
    public ResponseEntity<Map<String, String>> handleMismaPassword(MismaPasswordException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        
        // Detectar si es un error de foreign key relacionado con foto
        String mensaje = ex.getMessage();
        if (mensaje != null && mensaje.contains("id_foto") && mensaje.contains("Cannot delete")) {
            error.put("error", "No se puede eliminar la foto porque está siendo utilizada por otros registros");
            error.put("detalle", "La foto está asociada a uno o más integrantes, selecciones o publicaciones");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        
        // Otros errores de integridad
        error.put("error", "Error de integridad de datos");
        error.put("detalle", "La operación viola una restricción de base de datos");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // DTO interno para estandarizar el cuerpo de error
    public record ErrorResponse(int status, String mensaje, long timestamp) {}
}

