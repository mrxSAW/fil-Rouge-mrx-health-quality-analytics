package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentRiskDTO   implements Serializable {

    private Long departmentId;

    private String departmentName;

    private long criticalIncidents;

    private long nonCompliantCriteria;

    private long overdueActions;

    private double riskScore;
}