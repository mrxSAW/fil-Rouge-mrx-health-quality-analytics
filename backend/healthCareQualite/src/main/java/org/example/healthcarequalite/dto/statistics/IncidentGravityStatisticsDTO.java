package org.example.healthcarequalite.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.healthcarequalite.enums.IncidentGravity;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentGravityStatisticsDTO implements Serializable {

    private IncidentGravity gravity;

    private Long totalIncidents;
}