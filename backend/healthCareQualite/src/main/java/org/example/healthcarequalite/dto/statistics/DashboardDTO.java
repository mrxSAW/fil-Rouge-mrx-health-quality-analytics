package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

@Data
public class DashboardDTO {

    private long totalIncidents;

    private long totalAudits;

    private long criticalIncidents;

    private long overdueActions;

    private Double conformityRate;

    private Double qualityScore;

    private double riskScore;
}