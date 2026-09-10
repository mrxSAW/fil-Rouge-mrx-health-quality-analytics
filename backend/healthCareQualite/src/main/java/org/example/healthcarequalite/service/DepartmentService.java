package org.example.healthcarequalite.service;

import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.dto.department.DepartmentGetDTO;
import org.example.healthcarequalite.dto.department.DepartmentPostDTO;
import org.example.healthcarequalite.dto.department.DepartmentUpdateDTO;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.DepartmentMapper;
import org.example.healthcarequalite.repository.DepartmentRepository;
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

  public DepartmentGetDTO create(DepartmentPostDTO departmentPostDTO) {
    Department department =  departmentMapper.toEntity(departmentPostDTO);

    Department savedDepartment = departmentRepository.save(department);

    return departmentMapper.toGetDTO(savedDepartment);
  }

  public Page<DepartmentGetDTO> findAll(Pageable pageable) {

    Page<Department> departments = departmentRepository.findAll(pageable);

    return departments.map(departmentMapper::toGetDTO);
  }

  public DepartmentGetDTO findById(Long id) {

    Department department = findEntityById(id);

    return departmentMapper.toGetDTO(department);
  }

  public DepartmentGetDTO update(Long id, DepartmentUpdateDTO departmentUpdateDTO) {
    Department department = findEntityById(id);

    departmentMapper.updateDepartment(departmentUpdateDTO, department);

    Department updatedDepartment = departmentRepository.save(department);

    return departmentMapper.toGetDTO(updatedDepartment);
  }

  public void delete(Long id) {

    Department department = findEntityById(id);

    departmentRepository.delete(department);
  }

  private Department findEntityById(Long id) {

    return departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Département introuvable : " + id));
  }




}