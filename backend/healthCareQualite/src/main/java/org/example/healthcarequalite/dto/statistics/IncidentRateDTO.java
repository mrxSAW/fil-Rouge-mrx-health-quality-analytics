package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

@Data
public class IncidentRateDTO {

    private Long departmentId;

    private String departmentName;

    private Integer year;

    private Integer month;

    private long totalIncidents;

    private Integer admissionCount;

    private Double incidentsPerThousandAdmissions;
}