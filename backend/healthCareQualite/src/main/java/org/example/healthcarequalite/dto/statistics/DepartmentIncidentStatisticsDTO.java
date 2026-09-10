package org.example.healthcarequalite.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentIncidentStatisticsDTO {

    private Long departmentId;
    private String departmentName;
    private long totalIncidents;
}