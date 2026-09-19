package org.example.healthcarequalite.mapper;

import org.example.healthcarequalite.dto.department.DepartmentGetDTO;
import org.example.healthcarequalite.dto.department.DepartmentPostDTO;
import org.example.healthcarequalite.dto.department.DepartmentUpdateDTO;
import org.example.healthcarequalite.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    Department toEntity(DepartmentPostDTO departmentPostDTO);

    DepartmentGetDTO toGetDTO(Department department);

    @Mapping(target = "id", ignore = true)
    void updateDepartment(
            DepartmentUpdateDTO departmentUpdateDTO,
            @MappingTarget Department department
    );
}