package org.example.healthcarequalite.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.healthcarequalite.enums.IncidentGravity;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentGravityStatisticsDTO {

    private IncidentGravity gravity;

    private Long totalIncidents;
}