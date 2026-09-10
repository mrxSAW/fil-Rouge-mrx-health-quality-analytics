package org.example.healthcarequalite.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank(message = "L’adresse e-mail est obligatoire")
    @Email(message = "L’adresse e-mail est invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}