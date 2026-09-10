package org.example.healthcarequalite.mapper;

import org.example.healthcarequalite.entity.Incident;
import org.example.healthcarequalite.dto.incident.IncidentGetDTO;
import org.example.healthcarequalite.dto.incident.IncidentPostDTO;
import org.example.healthcarequalite.dto.incident.IncidentUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IncidentMapper {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "reporter.id", target = "reporterId")
    @Mapping(target = "reporterName", expression = "java(incident.getReporter().getFirstName() + \" \" + incident.getReporter().getLastName())")
    IncidentGetDTO toGetDTO(Incident incident);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    Incident toEntity(IncidentPostDTO incidentPostDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    void updateIncident(
            IncidentUpdateDTO incidentUpdateDTO,
            @MappingTarget Incident incident
    );
}