package org.example.healthcarequalite.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.healthcarequalite.enums.Role;

@Data
public class UserRoleDTO {

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}