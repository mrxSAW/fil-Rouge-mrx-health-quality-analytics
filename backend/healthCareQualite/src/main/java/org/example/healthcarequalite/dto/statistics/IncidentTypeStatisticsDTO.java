package org.example.healthcarequalite.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.healthcarequalite.enums.IncidentType;import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentTypeStatisticsDTO implements Serializable {

    private IncidentType type;

    private Long totalIncidents;
}