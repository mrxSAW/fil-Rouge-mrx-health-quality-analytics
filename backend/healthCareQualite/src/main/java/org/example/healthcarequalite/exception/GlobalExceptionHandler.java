package org.example.healthcarequalite.exception;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
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
  public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {

    Map<String, String> response = new LinkedHashMap<>();

    response.put( "error", "Accès refusé" );
    response.put( "message", "Vous n'avez pas les droits nécessaires pour effectuer cette opération." );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }


  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, String>> handleInvalidParameter(MethodArgumentTypeMismatchException ex) {

    String message;

    if (LocalDate.class.equals(ex.getRequiredType())) {message = "Le paramètre '" + ex.getName()
              + "' doit être une date valide au format AAAA-MM-JJ"
              + ", par exemple 2026-09-01.";
    } else {
      message = "La valeur du paramètre '"
              + ex.getName() + "' est invalide.";
    }

    Map<String, String> response = new LinkedHashMap<>();
    response.put("error", "Paramètre invalide");
    response.put("message", message);

    return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(response);
  }


}
