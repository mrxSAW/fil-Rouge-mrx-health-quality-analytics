package org.example.healthcarequalite.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsGetDTO;
import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsPostDTO;
import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsUpdateDTO;
import org.example.healthcarequalite.service.MonthlyAdmissionsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/monthly-admissions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'QUALITY_MANAGER')")
public class MonthlyAdmissionsController {

    private final MonthlyAdmissionsService monthlyAdmissionsService;

    public MonthlyAdmissionsController( MonthlyAdmissionsService monthlyAdmissionsService ) {

        this.monthlyAdmissionsService = monthlyAdmissionsService;
    }

    @PostMapping
    public ResponseEntity<MonthlyAdmissionsGetDTO> create( @Valid @RequestBody MonthlyAdmissionsPostDTO dto ) {

        MonthlyAdmissionsGetDTO admissions =  monthlyAdmissionsService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(admissions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonthlyAdmissionsGetDTO> findById( @PathVariable Long id ) {

        MonthlyAdmissionsGetDTO admissions = monthlyAdmissionsService.findById(id);

        return ResponseEntity.ok(admissions);
    }

    @GetMapping
    public ResponseEntity<Page<MonthlyAdmissionsGetDTO>> findAll(
            @RequestParam(required = false) Long departmentId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = createPageable(page, size, direction);

        Page<MonthlyAdmissionsGetDTO> admissions = monthlyAdmissionsService.findAll(departmentId, pageable);

        return ResponseEntity.ok(admissions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonthlyAdmissionsGetDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody MonthlyAdmissionsUpdateDTO dto) {

        MonthlyAdmissionsGetDTO admissions = monthlyAdmissionsService.update(id, dto);

        return ResponseEntity.ok(admissions);
    }

    private Pageable createPageable( int page, int size, String direction) {

        if (page < 0) {
            throw new IllegalArgumentException( "Le numéro de page doit être positif ou égal à zéro" );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException( "La taille de page doit être comprise entre 1 et 100" );
        }

        Sort.Direction sortDirection;

        if (direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else if (direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException( "La direction doit être asc ou desc" );
        }

        Sort sort = Sort.by(sortDirection, "year", "month", "id");

        return PageRequest.of(page, size, sort);
    }




}