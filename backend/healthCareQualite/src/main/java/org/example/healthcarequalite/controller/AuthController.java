package org.example.healthcarequalite.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.healthcarequalite.dto.Auth.AuthRequest;
import org.example.healthcarequalite.dto.Auth.AuthResponse;
import org.example.healthcarequalite.dto.Auth.RegisterRequest;
import org.example.healthcarequalite.service.AuthService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService service;

  @PostMapping("/register")
  ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){

    return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
  }


  @PostMapping("/login")
  AuthResponse login(@Valid @RequestBody AuthRequest request){

    return service.login(request);
  }






}
