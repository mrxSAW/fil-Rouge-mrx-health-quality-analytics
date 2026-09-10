package org.example.healthcarequalite.mapper;

import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsGetDTO;
import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsPostDTO;
import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsUpdateDTO;
import org.example.healthcarequalite.entity.MonthlyAdmissions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MonthlyAdmissionsMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    MonthlyAdmissions toEntity(MonthlyAdmissionsPostDTO dto);

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    MonthlyAdmissionsGetDTO toGetDTO(MonthlyAdmissions admissions);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "month", ignore = true)
    void updateFromDTO(MonthlyAdmissionsUpdateDTO dto, @MappingTarget MonthlyAdmissions admissions);
}