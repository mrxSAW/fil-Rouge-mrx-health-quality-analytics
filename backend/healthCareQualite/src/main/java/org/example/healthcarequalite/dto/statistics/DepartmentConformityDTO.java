package org.example.healthcarequalite.dto.statistics;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentConformityDTO implements Serializable  {

    private Long departmentId;

    private String departmentName;

    private long totalCriteria;

    private long compliantCriteria;

    private Double conformityRate;
}