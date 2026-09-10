package org.example.healthcarequalite.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.healthcarequalite.enums.IncidentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentTypeStatisticsDTO {

    private IncidentType type;

    private Long totalIncidents;
}