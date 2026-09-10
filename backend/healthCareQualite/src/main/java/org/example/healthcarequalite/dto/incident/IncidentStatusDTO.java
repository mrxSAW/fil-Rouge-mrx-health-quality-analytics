package org.example.healthcarequalite.dto.incident;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.healthcarequalite.enums.IncidentStatus;

@Data
public class IncidentStatusDTO {

    @NotNull(message = "Le statut est obligatoire")
    private IncidentStatus status;
}