package org.example.healthcarequalite.dto.incident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentType;

import java.time.LocalDate;

@Data
public class IncidentUpdateDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    private Long departmentId;


    @NotNull(message = "Le type est obligatoire")
    private IncidentType type;

    @NotNull(message = "La gravité est obligatoire")
    private IncidentGravity gravity;

    @NotNull(message = "La date de l'incident est obligatoire")
    private LocalDate incidentDate;





}