package org.example.healthcarequalite.dto.admissions;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MonthlyAdmissionsUpdateDTO {

    @NotNull(message = "Le nombre d'admissions est obligatoire")
    @Min(value = 0, message = "Le nombre d'admissions ne peut pas être négatif")
    private Integer admissionCount;
}