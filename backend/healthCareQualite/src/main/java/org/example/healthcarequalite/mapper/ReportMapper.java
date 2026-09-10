package org.example.healthcarequalite.mapper;

import org.example.healthcarequalite.dto.report.ReportGetDTO;
import org.example.healthcarequalite.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    ReportGetDTO toGetDTO(Report report);
}