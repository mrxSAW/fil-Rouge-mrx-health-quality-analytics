package org.example.healthcarequalite.dto.statistics;

import lombok.Data;
import java.io.Serializable;

@Data
public class IncidentStatisticsDTO implements Serializable  {

    private long totalIncidents;

    private long criticalIncidents;
}