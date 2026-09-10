package org.example.healthcarequalite.dto.user;

import lombok.Data;
import org.example.healthcarequalite.enums.Role;

@Data
public class UserGetDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private Long departmentId;

    private String departmentName;
}