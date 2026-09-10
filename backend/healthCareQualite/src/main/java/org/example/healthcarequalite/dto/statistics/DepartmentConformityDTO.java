package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

@Data
public class DepartmentConformityDTO {

    private Long departmentId;

    private String departmentName;

    private long totalCriteria;

    private long compliantCriteria;

    private Double conformityRate;
}