package org.example.healthcarequalite.dto.audit;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AuditGetDTO {

    private Long id;

    private String title;

    private LocalDate auditDate;

    private Integer score;

    private Integer totalCriteria;

    private Integer compliantCriteria;

    private Double conformityRate;

    private String observations;

    private Long departmentId;

    private String departmentName;
}