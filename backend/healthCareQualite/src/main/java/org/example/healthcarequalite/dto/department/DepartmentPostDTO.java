package org.example.healthcarequalite.dto.department;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentPostDTO {

    @NotBlank(message = "Le nom du département est obligatoire")
    private String name;

    private String description;
}