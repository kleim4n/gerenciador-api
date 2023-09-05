package br.com.kleiman.gerenciador.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    public static class UnprocessableException extends RuntimeException {
        public UnprocessableException(String message) {
            super(message);
        }
    }
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }
    @ExceptionHandler(value = UnprocessableException.class)
    public ResponseEntity<Map<String, String>> handleConflictUnprocessable(UnprocessableException ex) {
        return ResponseEntity.status(422).body(Map.of("message", ex.getMessage()));
    }
    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflictSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException ex) {
        return ResponseEntity.status(422).body(Map.of("message", ex.getMessage()));
    }
    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleConflictNotFound(NotFoundException ex) {
        return ResponseEntity.status(404).body(Map.of("message", ex.getMessage()));
    }
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleConflictBadRequest(BadRequestException ex) {
        return ResponseEntity.status(400).body(Map.of("message", ex.getMessage()));
    }
}
