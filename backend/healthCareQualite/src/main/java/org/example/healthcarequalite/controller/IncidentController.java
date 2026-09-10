package org.example.healthcarequalite.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.healthcarequalite.dto.incident.IncidentGetDTO;
import org.example.healthcarequalite.dto.incident.IncidentPostDTO;
import org.example.healthcarequalite.dto.incident.IncidentStatusDTO;
import org.example.healthcarequalite.dto.incident.IncidentUpdateDTO;
import org.example.healthcarequalite.dto.statistics.IncidentStatisticsDTO;
import org.example.healthcarequalite.service.IncidentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentStatus;
import org.example.healthcarequalite.enums.IncidentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@SecurityRequirement(name = "bearerAuth")
public class IncidentController {

  private final IncidentService incidentService;

  public IncidentController(IncidentService incidentService) {
    this.incidentService = incidentService;
  }



  @GetMapping
  public ResponseEntity<Page<IncidentGetDTO>> search(
          @RequestParam(required = false) IncidentType type, @RequestParam(required = false) IncidentGravity gravity,
          @RequestParam(required = false) IncidentStatus status, @RequestParam(required = false) Long departmentId,
          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
          @RequestParam(defaultValue = "incidentDate") String sort, @RequestParam(defaultValue = "desc") String direction,
          Authentication authentication) {

    Pageable pageable = createPageable(page, size, sort, direction);

    String connectedEmail = authentication.getName();

    Page<IncidentGetDTO> incidents = incidentService.search(
            type, gravity, status, departmentId, connectedEmail, pageable
    );

    return ResponseEntity.ok(incidents);
  }




  @GetMapping("staff/{id}")
  public ResponseEntity<IncidentGetDTO> findForStaf( @PathVariable Long id, Authentication authentication) {

    String connectedEmail = authentication.getName();

    IncidentGetDTO incident = incidentService.findForStaf(id, connectedEmail);

    return ResponseEntity.ok(incident);
  }

  @PostMapping
  public ResponseEntity<IncidentGetDTO> create(@Valid @RequestBody IncidentPostDTO incidentPostDTO, Authentication authentication) {
    IncidentGetDTO createdIncident = incidentService.create(incidentPostDTO, authentication.getName());

    return ResponseEntity.status(HttpStatus.CREATED).body(createdIncident);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','QUALITY_MANAGER','QHSE_MANAGER')")
  public ResponseEntity<IncidentGetDTO> update(@PathVariable Long id, @Valid @RequestBody IncidentUpdateDTO incidentUpdateDTO) {
    IncidentGetDTO updatedIncident = incidentService.update(id, incidentUpdateDTO);

    return ResponseEntity.ok(updatedIncident);
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasAnyRole('ADMIN','QUALITY_MANAGER','QHSE_MANAGER')")
  public ResponseEntity<IncidentGetDTO> updateStatus(@PathVariable Long id, @Valid @RequestBody IncidentStatusDTO incidentStatusDTO) {
    IncidentGetDTO updatedIncident = incidentService.updateStatus(id, incidentStatusDTO);

    return ResponseEntity.ok(updatedIncident);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    incidentService.delete(id);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/count/critical")
  @PreAuthorize("hasAnyRole('ADMIN', 'QUALITY_MANAGER', 'QHSE_MANAGER')")
  public ResponseEntity<Long> countCriticalIncidents() {

    long criticalCount = incidentService.countCriticalIncidents();

    return ResponseEntity.ok(criticalCount);
  }


  @GetMapping("/count")
  @PreAuthorize("hasAnyRole('ADMIN', 'QUALITY_MANAGER', 'QHSE_MANAGER')")
  public ResponseEntity<Long> countAllIncidents() {

    long total = incidentService.countAllIncidents();

    return ResponseEntity.ok(total);
  }


  @GetMapping("/statistics")
  @PreAuthorize("hasAnyRole('ADMIN', 'QUALITY_MANAGER', 'QHSE_MANAGER')")
  public ResponseEntity<IncidentStatisticsDTO> getStatistics() {

    IncidentStatisticsDTO statistics = incidentService.getStatistics();

    return ResponseEntity.ok(statistics);
  }


  private Pageable createPageable(int page, int size, String sort, String direction) {

    if (page < 0) {
      throw new IllegalArgumentException(
              "Le numéro de page doit être positif ou égal à zéro");
    }

    if (size < 1 || size > 100) {
      throw new IllegalArgumentException(
              "La taille de page doit être comprise entre 1 et 100");
    }

    if (!sort.equals("incidentDate") && !sort.equals("gravity") && !sort.equals("status") && !sort.equals("department.name") && !sort.equals("id")) {
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