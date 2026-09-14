package org.example.healthcarequalite.service;

import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.dto.department.DepartmentGetDTO;
import org.example.healthcarequalite.dto.department.DepartmentPostDTO;
import org.example.healthcarequalite.dto.department.DepartmentUpdateDTO;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.DepartmentMapper;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

  private final DepartmentRepository departmentRepository;
  private final DepartmentMapper departmentMapper;

  public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
    this.departmentRepository = departmentRepository;
    this.departmentMapper = departmentMapper;
  }

  @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
  public DepartmentGetDTO create(DepartmentPostDTO departmentPostDTO) {
    Department department =  departmentMapper.toEntity(departmentPostDTO);

    Department savedDepartment = departmentRepository.save(department);

    return departmentMapper.toGetDTO(savedDepartment);
  }

  @Cacheable(cacheNames = "lists", key = "{#root.targetClass.simpleName, #root.methodName, #root.args}", condition = "!T(org.springframework.security.core.context.SecurityContextHolder)" + ".getContext().getAuthentication().getAuthorities()" + ".![authority].contains('ROLE_STAFF')")
  public Page<DepartmentGetDTO> findAll(Pageable pageable) {

    Page<Department> departments = departmentRepository.findAll(pageable);

    return departments.map(departmentMapper::toGetDTO);
  }

  public DepartmentGetDTO findById(Long id) {

    Department department = findEntityById(id);

    return departmentMapper.toGetDTO(department);
  }

  @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
  public DepartmentGetDTO update(Long id, DepartmentUpdateDTO departmentUpdateDTO) {
    Department department = findEntityById(id);

    departmentMapper.updateDepartment(departmentUpdateDTO, department);

    Department updatedDepartment = departmentRepository.save(department);

    return departmentMapper.toGetDTO(updatedDepartment);
  }

  @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
  public void delete(Long id) {

    Department department = findEntityById(id);

    departmentRepository.delete(department);
  }

  private Department findEntityById(Long id) {

    return departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Département introuvable : " + id));
  }




}