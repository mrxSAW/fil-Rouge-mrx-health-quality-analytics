package org.example.healthcarequalite.mapper;

import org.example.healthcarequalite.dto.audit.AuditGetDTO;
import org.example.healthcarequalite.dto.audit.AuditPostDTO;
import org.example.healthcarequalite.dto.audit.AuditUpdateDTO;
import org.example.healthcarequalite.entity.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "conformityRate", ignore = true)
    Audit toEntity(AuditPostDTO dto);

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    AuditGetDTO toGetDTO(Audit audit);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "conformityRate", ignore = true)
    void updateAuditFromDTO(AuditUpdateDTO dto, @MappingTarget Audit audit);
}