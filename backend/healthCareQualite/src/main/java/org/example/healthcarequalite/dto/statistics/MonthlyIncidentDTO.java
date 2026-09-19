package org.example.healthcarequalite.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyIncidentDTO implements Serializable {

    private Integer year;

    private Integer month;

    private Long totalIncidents;
}