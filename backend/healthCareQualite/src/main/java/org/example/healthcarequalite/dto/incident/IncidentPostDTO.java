package org.example.healthcarequalite.dto.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentType;

import java.time.LocalDate;

@Data
public class IncidentPostDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le département est obligatoire")
    private Long departmentId;

    @NotNull(message = "Le type est obligatoire")
    private IncidentType type;

    @NotNull(message = "La gravity est obligatoire")
    private IncidentGravity gravity;

    @NotNull(message = "La date  est obligatoire")
    private LocalDate incidentDate;




}