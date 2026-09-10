package org.example.healthcarequalite.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentQhseReportDTO {

    private Long departmentId;
    private String departmentName;

    private long criticalIncidents;
    private long nonCompliantCriteria;
    private long overdueActions;

    private long totalCriteria;
    private long compliantCriteria;

    private Double conformityRate;
    private double riskScore;
}