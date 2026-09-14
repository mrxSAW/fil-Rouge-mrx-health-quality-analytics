package org.example.healthcarequalite.dto.statistics;

import lombok.Data;
import java.io.Serializable;

@Data
public class DashboardDTO implements Serializable  {

    private long totalIncidents;

    private long totalAudits;

    private long criticalIncidents;

    private long overdueActions;

    private Double conformityRate;

    private Double qualityScore;

    private double riskScore;
}