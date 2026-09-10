package org.example.healthcarequalite.mapper;

import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionGetDTO;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionPostDTO;
import org.example.healthcarequalite.entity.CorrectiveAction;
import org.example.healthcarequalite.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionUpdateDTO;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CorrectiveActionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "incident", ignore = true)
    @Mapping(target = "responsibleUser", ignore = true)
    CorrectiveAction toEntity(CorrectiveActionPostDTO dto);

    @Mapping(source = "incident.id", target = "incidentId")
    @Mapping(source = "incident.title", target = "incidentTitle")
    @Mapping(source = "responsibleUser.id", target = "responsibleUserId")
    @Mapping(source = "responsibleUser", target = "responsibleUserName", qualifiedByName = "fullName")
    CorrectiveActionGetDTO toGetDTO(CorrectiveAction action);

    @Named("fullName")
    default String fullName(User user) {

        if (user == null) {return null;}

        return user.getFirstName() + " " + user.getLastName();
    }




    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "incident", ignore = true)
    @Mapping(target = "responsibleUser", ignore = true)
    void updateCorrectiveActionFromDTO(CorrectiveActionUpdateDTO dto, @MappingTarget CorrectiveAction action);





}