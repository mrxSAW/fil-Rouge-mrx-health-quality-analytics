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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(cacheNames = "lists", key = "{#root.targetClass.simpleName, #root.methodName, #root.args}", condition = "!T(org.springframework.security.core.context.SecurityContextHolder)" + ".getContext().getAuthentication().getAuthorities()" + ".![authority].contains('ROLE_STAFF')")
    public Page<UserGetDTO> findAll(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> userMapper.toGetDTO(user));
    }

    public UserGetDTO findById(Long id) {

        User user = findEntityById(id);

        return userMapper.toGetDTO(user);
    }

    @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
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

    @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
    public UserGetDTO updateRole(Long id, UserRoleDTO userRoleDTO) {
        User user = findEntityById(id);

        user.setRole(userRoleDTO.getRole());

        User updatedUser = userRepository.save(user);

        return userMapper.toGetDTO(user);
    }

    @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
    public void delete(Long id) {

        User user = findEntityById(id);

        userRepository.delete(user);
    }

    private User findEntityById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + id));
    }


}