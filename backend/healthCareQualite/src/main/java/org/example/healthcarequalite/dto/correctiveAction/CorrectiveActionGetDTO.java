package org.example.healthcarequalite.dto.correctiveAction;

import lombok.Data;
import org.example.healthcarequalite.enums.CorrectiveActionStatus;

import java.time.LocalDate;

@Data
public class CorrectiveActionGetDTO {

    private Long id;

    private String title;

    private String description;

    private CorrectiveActionStatus status;

    private LocalDate deadline;

    private Long incidentId;

    private String incidentTitle;

    private Long responsibleUserId;

    private String responsibleUserName;
}