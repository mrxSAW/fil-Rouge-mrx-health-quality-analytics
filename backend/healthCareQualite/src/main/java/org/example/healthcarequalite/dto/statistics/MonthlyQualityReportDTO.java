package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

@Data
public class MonthlyQualityReportDTO {

    private Integer year;

    private Integer month;

    private long totalIncidents;

    private long criticalIncidents;

    private long totalAudits;

    private Double conformityRate;

    private Double qualityScore;
}