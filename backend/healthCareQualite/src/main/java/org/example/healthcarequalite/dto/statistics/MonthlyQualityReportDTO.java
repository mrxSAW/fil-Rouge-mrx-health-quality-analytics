package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

import java.io.Serializable;


@Data
public class MonthlyQualityReportDTO implements Serializable {

    private Integer year;

    private Integer month;

    private long totalIncidents;

    private long criticalIncidents;

    private long totalAudits;

    private Double conformityRate;

    private Double qualityScore;
}