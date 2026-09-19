package org.example.healthcarequalite.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionGetDTO;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionPostDTO;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionStatusDTO;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionUpdateDTO;
import org.example.healthcarequalite.dto.user.ResponsibleUserDTO;
import org.example.healthcarequalite.service.CorrectiveActionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/corrective-actions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'QHSE_MANAGER', 'STAFF')")
public class CorrectiveActionController {

    private final CorrectiveActionService correctiveActionService;



    public CorrectiveActionController(CorrectiveActionService correctiveActionService) {

        this.correctiveActionService = correctiveActionService;

    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'QHSE_MANAGER')")
    public ResponseEntity<CorrectiveActionGetDTO> create(@Valid @RequestBody CorrectiveActionPostDTO dto) {

        CorrectiveActionGetDTO action = correctiveActionService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(action);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CorrectiveActionGetDTO> findById( @PathVariable Long id, Authentication authentication) {

        String connectedEmail = authentication.getName();

        CorrectiveActionGetDTO action = correctiveActionService.findById(id, connectedEmail);

        return ResponseEntity.ok(action);
    }

    @GetMapping
    public ResponseEntity<Page<CorrectiveActionGetDTO>> findAll(
            @RequestParam(required = false) Long responsibleUserId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "deadline") String sort,
            @RequestParam(defaultValue = "asc") String direction, Authentication authentication) {

        Pageable pageable = createPageable(page, size, sort, direction);
        String connectedEmail = authentication.getName();

        Page<CorrectiveActionGetDTO> actions =
                correctiveActionService.findAll(responsibleUserId, connectedEmail, pageable);

        return ResponseEntity.ok(actions);
    }



    @GetMapping("/overdue")
    public ResponseEntity<Page<CorrectiveActionGetDTO>> findOverdue(
            @RequestParam(required = false) Long responsibleUserId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "deadline") String sort,
            @RequestParam(defaultValue = "asc") String direction, Authentication authentication) {

        Pageable pageable = createPageable(page, size, sort, direction);
        String connectedEmail = authentication.getName();

        Page<CorrectiveActionGetDTO> actions =
                correctiveActionService.findOverdue( responsibleUserId, connectedEmail, pageable );

        return ResponseEntity.ok(actions);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'QHSE_MANAGER')")
    public ResponseEntity<CorrectiveActionGetDTO> update( @PathVariable Long id, @Valid @RequestBody CorrectiveActionUpdateDTO dto) {

        CorrectiveActionGetDTO action = correctiveActionService.update(id, dto);

        return ResponseEntity.ok(action);
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'QHSE_MANAGER')")
    public ResponseEntity<CorrectiveActionGetDTO> updateStatus( @PathVariable Long id, @Valid @RequestBody CorrectiveActionStatusDTO dto) {

        CorrectiveActionGetDTO action = correctiveActionService.updateStatus(id, dto);

        return ResponseEntity.ok(action);
    }


    @GetMapping("/count/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'QHSE_MANAGER')")
    public ResponseEntity<Long> countOverdueActions() {

        long total = correctiveActionService.countOverdueActions();

        return ResponseEntity.ok(total);
    }



    private Pageable createPageable( int page, int size, String sort, String direction) {

        if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page doit être positif ou égal à zéro");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("La taille de page doit être comprise entre 1 et 100");
        }

        if (!sort.equals("deadline") && !sort.equals("status") && !sort.equals("title") && !sort.equals("id")) {
            throw new IllegalArgumentException("Champ de tri non autorisé");
        }

        Sort.Direction sortDirection;

        if (direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else if (direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException(
                    "La direction doit être asc ou desc");
        }

        return PageRequest.of(page, size, Sort.by(sortDirection, sort));
    }




    @GetMapping("/responsible-users")
    @PreAuthorize("hasAnyRole('ADMIN', 'QHSE_MANAGER')")
    public ResponseEntity<Page<ResponsibleUserDTO>> findResponsibleUsers( @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {

        if (page < 0) {
            throw new IllegalArgumentException( "Le numéro de page doit être positif ou égal à zéro" );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException( "La taille de page doit être comprise entre 1 et 100");
        }

        Pageable pageable = PageRequest.of( page, size, Sort.by("lastName", "firstName", "id") );

        Page<ResponsibleUserDTO> users = correctiveActionService.findResponsibleUsers(pageable);

        return ResponseEntity.ok(users);
    }


}