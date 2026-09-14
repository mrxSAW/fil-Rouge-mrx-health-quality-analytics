package org.example.healthcarequalite.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentIncidentStatisticsDTO   implements Serializable {

    private Long departmentId;
    private String departmentName;
    private long totalIncidents;
}