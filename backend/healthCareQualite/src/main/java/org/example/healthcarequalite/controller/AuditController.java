package org.example.healthcarequalite.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.healthcarequalite.dto.audit.AuditGetDTO;
import org.example.healthcarequalite.dto.audit.AuditPostDTO;
import org.example.healthcarequalite.dto.audit.AuditUpdateDTO;
import org.example.healthcarequalite.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audits")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'QUALITY_MANAGER')")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {

        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<AuditGetDTO> create(@Valid @RequestBody AuditPostDTO dto) {

        AuditGetDTO audit = auditService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(audit);
    }


    @GetMapping("/{id}")
    public ResponseEntity<AuditGetDTO> findById(@PathVariable Long id) {

        AuditGetDTO audit = auditService.findById(id);

        return ResponseEntity.ok(audit);
    }


    @GetMapping
    public ResponseEntity<Page<AuditGetDTO>> findAll(@RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "auditDate") String sort, @RequestParam(defaultValue = "desc") String direction) {

        Pageable pageable = createPageable(page, size, sort, direction);

        Page<AuditGetDTO> audits = auditService.findAll(departmentId, pageable);

        return ResponseEntity.ok(audits);
    }


    @PutMapping("/{id}")
    public ResponseEntity<AuditGetDTO> update( @PathVariable Long id, @Valid @RequestBody AuditUpdateDTO dto) {

        AuditGetDTO audit = auditService.update(id, dto);

        return ResponseEntity.ok(audit);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        auditService.delete(id);

        return ResponseEntity.noContent().build();
    }



    @GetMapping("/count/non-compliant-criteria")
    public ResponseEntity<Long> countNonCompliantCriteria() {

        long total = auditService.countNonCompliantCriteria();

        return ResponseEntity.ok(total);
    }


    private Pageable createPageable(int page, int size, String sort, String direction) {

        if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page doit être positif ou égal à zéro");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("La taille de page doit être comprise entre 1 et 100");
        }

        if (!sort.equals("auditDate") && !sort.equals("score") && !sort.equals("conformityRate") && !sort.equals("department.name") && !sort.equals("id")) {
            throw new IllegalArgumentException("Champ de tri non autorisé");
        }

        Sort.Direction sortDirection;

        if (direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else if (direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("La direction doit être asc ou desc");
        }

        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }


}