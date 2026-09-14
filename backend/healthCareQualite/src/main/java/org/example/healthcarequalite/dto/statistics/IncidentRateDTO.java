package org.example.healthcarequalite.dto.statistics;

import lombok.Data;
import java.io.Serializable;

@Data
public class IncidentRateDTO implements Serializable  {

    private Long departmentId;

    private String departmentName;

    private Integer year;

    private Integer month;

    private long totalIncidents;

    private Integer admissionCount;

    private Double incidentsPerThousandAdmissions;
}