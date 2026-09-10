package org.example.healthcarequalite.service;

import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.entity.User;
import org.example.healthcarequalite.dto.user.UserGetDTO;
import org.example.healthcarequalite.dto.user.UserRoleDTO;
import org.example.healthcarequalite.dto.user.UserUpdateDTO;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.UserMapper;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.example.healthcarequalite.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, DepartmentRepository departmentRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.userMapper = userMapper;
    }


    public Page<UserGetDTO> findAll(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        return users.map(userMapper::toGetDTO);
    }

    public UserGetDTO findById(Long id) {

        User user = findEntityById(id);

        return userMapper.toGetDTO(user);
    }

    public UserGetDTO update(Long id, UserUpdateDTO userUpdateDTO) {

        User user = findEntityById(id);
        User userWithSameEmail = userRepository.findByEmail(userUpdateDTO.getEmail()).orElse(null);

        if (userWithSameEmail != null && !userWithSameEmail.getId().equals(id)) {
            throw new IllegalArgumentException("Cette adresse e-mail est déjà utilisée");
        }

        userMapper.updateUser(userUpdateDTO, user);

        Long departmentId = userUpdateDTO.getDepartmentId();

        if (departmentId != null) {

            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Département introuvable : " + departmentId));

            user.setDepartment(department);

        } else {
            user.setDepartment(null);
        }

        User updatedUser = userRepository.save(user);

        return userMapper.toGetDTO(updatedUser);
    }

    public UserGetDTO updateRole(Long id, UserRoleDTO userRoleDTO) {
        User user = findEntityById(id);

        user.setRole(userRoleDTO.getRole());

        User updatedUser = userRepository.save(user);

        return userMapper.toGetDTO(user);
    }

    public void delete(Long id) {

        User user = findEntityById(id);

        userRepository.delete(user);
    }

    private User findEntityById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }


}