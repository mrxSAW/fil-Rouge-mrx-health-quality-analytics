package org.example.healthcarequalite.mapper;

import org.example.healthcarequalite.dto.user.UserGetDTO;
import org.example.healthcarequalite.dto.user.UserUpdateDTO;
import org.example.healthcarequalite.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    UserGetDTO toGetDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateUser( UserUpdateDTO userUpdateDTO, @MappingTarget User user );
}