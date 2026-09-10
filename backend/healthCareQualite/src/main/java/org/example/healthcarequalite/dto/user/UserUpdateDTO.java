package org.example.healthcarequalite.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    @NotBlank(message = "L’adresse e-mail est obligatoire")
    @Email(message = "L’adresse e-mail est invalide")
    private String email;

    private Long departmentId;
}