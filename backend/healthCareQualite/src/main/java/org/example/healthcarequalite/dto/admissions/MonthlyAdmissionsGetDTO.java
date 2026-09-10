package org.example.healthcarequalite.dto.admissions;

import lombok.Data;

@Data
public class MonthlyAdmissionsGetDTO {

    private Long id;

    private Long departmentId;

    private String departmentName;

    private Integer year;

    private Integer month;

    private Integer admissionCount;
}