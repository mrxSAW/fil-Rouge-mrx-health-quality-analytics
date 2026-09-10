package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

@Data
public class RiskStatisticsDTO {

    private long criticalIncidents;

    private long nonCompliantCriteria;

    private long overdueActions;

    private double riskScore;
}