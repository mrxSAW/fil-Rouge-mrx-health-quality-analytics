package org.example.healthcarequalite.dto.correctiveAction;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.healthcarequalite.enums.CorrectiveActionStatus;

@Data
public class CorrectiveActionStatusDTO {

    @NotNull(message = "Le statut est obligatoire")
    private CorrectiveActionStatus status;
}