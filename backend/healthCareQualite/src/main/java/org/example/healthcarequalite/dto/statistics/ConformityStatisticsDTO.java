package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

@Data
public class ConformityStatisticsDTO {

    private long totalCriteria;

    private long compliantCriteria;

    private Double conformityRate;
}