package org.example.healthcarequalite.dto.correctiveAction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CorrectiveActionUpdateDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "La date limite est obligatoire")
    private LocalDate deadline;

    @NotNull(message = "L'incident est obligatoire")
    @Positive(message = "L'identifiant de l'incident doit être positif")
    private Long incidentId;

    @NotNull(message = "Le responsable est obligatoire")
    @Positive(message = "L'identifiant du responsable doit être positif")
    private Long responsibleUserId;
}