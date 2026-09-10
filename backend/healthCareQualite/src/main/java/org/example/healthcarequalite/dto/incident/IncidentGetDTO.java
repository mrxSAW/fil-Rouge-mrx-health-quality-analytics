package org.example.healthcarequalite.dto.incident;

import lombok.Data;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentStatus;
import org.example.healthcarequalite.enums.IncidentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IncidentGetDTO {

    private Long id;

    private String title;

    private String description;

    private IncidentStatus status;

    private LocalDateTime createdAt;

    private Long departmentId;

    private String departmentName;

    private Long reporterId;

    private String reporterName;

    private IncidentType type;


    private IncidentGravity gravity;


    private LocalDate incidentDate;

}