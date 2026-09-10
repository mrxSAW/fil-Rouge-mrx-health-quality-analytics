package org.example.healthcarequalite.dto.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.healthcarequalite.enums.Role;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type;
    private Long userId;
    private Role role;
}