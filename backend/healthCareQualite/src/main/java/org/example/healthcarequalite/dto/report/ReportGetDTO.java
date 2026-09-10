package org.example.healthcarequalite.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.healthcarequalite.enums.ReportType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportGetDTO {

    private Long id;

    private String title;

    private ReportType type;

    private LocalDate createdAt;

    private String fileUrl;

    private Long departmentId;

    private String departmentName;
}