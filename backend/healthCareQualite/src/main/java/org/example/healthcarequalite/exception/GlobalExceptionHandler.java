package org.example.healthcarequalite.exception;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<Map<String,String>> notFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(404).body(Map.of("message",ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<Map<String,String>> badRequest(IllegalArgumentException ex) {

    return ResponseEntity.badRequest().body(Map.of("message",ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String,String>> validation(MethodArgumentNotValidException ex) {
    Map<String,String> errors=new LinkedHashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e->errors.put(e.getField(),e.getDefaultMessage()));
    return ResponseEntity.badRequest().body(errors);
  }


  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDenied(
          AccessDeniedException ex) {

    Map<String, String> response = new LinkedHashMap<>();

    response.put( "error", "Accès refusé" );
    response.put( "message", "Vous n'avez pas les droits nécessaires pour effectuer cette opération." );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }




}
