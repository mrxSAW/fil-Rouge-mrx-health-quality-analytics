package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConformityStatisticsDTO implements Serializable {

    private long totalCriteria;

    private long compliantCriteria;

    private Double conformityRate;
}