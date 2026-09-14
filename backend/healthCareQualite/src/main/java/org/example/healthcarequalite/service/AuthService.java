package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.Auth.AuthRequest;
import org.example.healthcarequalite.dto.Auth.AuthResponse;
import org.example.healthcarequalite.dto.Auth.RegisterRequest;
import org.example.healthcarequalite.enums.Role;
import org.example.healthcarequalite.entity.User;
import org.example.healthcarequalite.repository.UserRepository;
import org.example.healthcarequalite.security.JwtService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  public AuthResponse register(RegisterRequest request) {

    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("Cette adresse e-mail existe déjà");
    }

    User user = new User();

    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());

      String userName= user.getFirstName()+"_"+user.getLastName();

    user.setUsername(userName);
    user.setEmail(request.getEmail());
    user.setPassword( passwordEncoder.encode(request.getPassword()) );
    user.setRole(Role.STAFF);

    User savedUser = userRepository.save(user);

    String token = jwtService.generate(savedUser.getEmail());

    return new AuthResponse(
            token, "Bearer",
            savedUser.getId(),
            savedUser.getRole()
    );
  }

  public AuthResponse login(AuthRequest request) {

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword() );

    authenticationManager.authenticate(authenticationToken);

    User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

    String token = jwtService.generate(user.getEmail());

    return new AuthResponse(
            token,
            "Bearer",
            user.getId(),
            user.getRole()
    );
  }
}