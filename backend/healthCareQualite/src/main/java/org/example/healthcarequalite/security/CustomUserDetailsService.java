package org.example.healthcarequalite.security;

import lombok.RequiredArgsConstructor;
import org.example.healthcarequalite.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository users;
  @Override public UserDetails loadUserByUsername(String email) {
    return users.findByEmail(email)
      .map(u -> User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole().name()).build())
      .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
  }
}
