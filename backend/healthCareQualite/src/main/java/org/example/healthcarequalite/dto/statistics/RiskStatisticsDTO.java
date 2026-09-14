package org.example.healthcarequalite.dto.statistics;

import lombok.Data;
import java.io.Serializable;

@Data
public class RiskStatisticsDTO implements Serializable {

    private long criticalIncidents;

    private long nonCompliantCriteria;

    private long overdueActions;

    private double riskScore;
}