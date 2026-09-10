package org.example.healthcarequalite.dto.audit;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuditPostDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotNull(message = "La date d'audit est obligatoire")
    private LocalDate auditDate;

    @NotNull(message = "Le score est obligatoire")
    @Min(value = 0, message = "Le score doit être supérieur ou égal à 0")
    @Max(value = 100, message = "Le score doit être inférieur ou égal à 100")
    private Integer score;

    @NotNull(message = "Le nombre de critères est obligatoire")
    @Min(value = 1, message = "Le nombre de critères doit être au moins 1")
    private Integer totalCriteria;

    @NotNull(message = "Le nombre de critères conformes est obligatoire")
    @Min(value = 0, message = "Le nombre de critères conformes ne peut pas être négatif")
    private Integer compliantCriteria;

    private String observations;

    @NotNull(message = "Le département est obligatoire")
    @Positive(message = "L'identifiant du département doit être positif")
    private Long departmentId;
}