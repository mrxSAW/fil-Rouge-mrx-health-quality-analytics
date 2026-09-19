package org.example.healthcarequalite.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.healthcarequalite.dto.department.DepartmentGetDTO;
import org.example.healthcarequalite.dto.department.DepartmentPostDTO;
import org.example.healthcarequalite.dto.department.DepartmentUpdateDTO;
import org.example.healthcarequalite.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

  private final DepartmentService departmentService;

  public DepartmentController( DepartmentService departmentService) {
    this.departmentService = departmentService;
  }

  @GetMapping
  public ResponseEntity<Page<DepartmentGetDTO>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

    if (page < 0) {
      throw new IllegalArgumentException("Le numéro de page doit être positif ou égal à zéro");
    }

    if (size < 1 || size > 100) {
      throw new IllegalArgumentException("La taille de la page doit être comprise entre 1 et 100");
    }

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

    Page<DepartmentGetDTO> departments = departmentService.findAll(pageable);

    return ResponseEntity.ok(departments);
  }
  @GetMapping("/{id}")
  public ResponseEntity<DepartmentGetDTO> findById(@PathVariable Long id) {
    DepartmentGetDTO department = departmentService.findById(id);

    return ResponseEntity.ok(department);
  }



  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<DepartmentGetDTO> create( @Valid @RequestBody DepartmentPostDTO departmentPostDTO) {
    DepartmentGetDTO createdDepartment = departmentService.create(departmentPostDTO);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<DepartmentGetDTO> update( @PathVariable Long id, @Valid @RequestBody DepartmentUpdateDTO departmentUpdateDTO) {
    DepartmentGetDTO updatedDepartment = departmentService.update( id, departmentUpdateDTO );

    return ResponseEntity.ok(updatedDepartment);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    departmentService.delete(id);

    return ResponseEntity.noContent().build();
  }




}