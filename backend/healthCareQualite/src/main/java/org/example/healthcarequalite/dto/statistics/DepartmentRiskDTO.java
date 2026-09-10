package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

@Data
public class DepartmentRiskDTO {

    private Long departmentId;

    private String departmentName;

    private long criticalIncidents;

    private long nonCompliantCriteria;

    private long overdueActions;

    private double riskScore;
}