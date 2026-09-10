package org.example.healthcarequalite.dto.admissions;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MonthlyAdmissionsPostDTO {

    @NotNull(message = "Le département est obligatoire")
    @Positive(message = "L'identifiant du département doit être positif")
    private Long departmentId;

    @NotNull(message = "L'année est obligatoire")
    @Min(value = 1, message = "L'année doit être positive")
    @Max(value = 9999, message = "L'année ne doit pas dépasser 9999")
    private Integer year;

    @NotNull(message = "Le mois est obligatoire")
    @Min(value = 1, message = "Le mois doit être compris entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être compris entre 1 et 12")
    private Integer month;

    @NotNull(message = "Le nombre d'admissions est obligatoire")
    @Min(value = 0, message = "Le nombre d'admissions ne peut pas être négatif")
    private Integer admissionCount;
}