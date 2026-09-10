package org.example.healthcarequalite.config;

import lombok.RequiredArgsConstructor;
import org.example.healthcarequalite.security.CustomAccessDeniedHandler;
import org.example.healthcarequalite.security.JwtAuthenticationEntryPoint;
import org.example.healthcarequalite.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;
  private final UserDetailsService userDetailsService;
  private final JwtAuthenticationEntryPoint authenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;

  @Bean
  PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider p=new DaoAuthenticationProvider(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
  }


  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
    return c.getAuthenticationManager();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http.csrf(
            csrf->csrf.disable()).cors(cors->{}).

            sessionManagement(
                    s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .exceptionHandling(
                    exception -> exception
                            .authenticationEntryPoint(authenticationEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler)
            )


      .authorizeHttpRequests(
              a->a.requestMatchers(
                      "/api/auth/**",
                      "/swagger-ui/**",
                      "/swagger-ui.html",
                      "/v3/api-docs/**").permitAll()

                      .anyRequest().authenticated())

      .authenticationProvider(
              authenticationProvider()).addFilterBefore(
                      jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
  }
}
