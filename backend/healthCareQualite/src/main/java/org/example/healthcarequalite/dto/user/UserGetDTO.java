package org.example.healthcarequalite.dto.user;

import lombok.Data;
import org.example.healthcarequalite.enums.Role;

import java.io.Serializable;

@Data
public class UserGetDTO implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private Long departmentId;

    private String departmentName;
}