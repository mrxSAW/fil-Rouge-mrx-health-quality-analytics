package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

@Data
public class IncidentStatisticsDTO {

    private long totalIncidents;

    private long criticalIncidents;
}