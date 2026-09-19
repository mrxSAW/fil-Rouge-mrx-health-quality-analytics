package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.incident.IncidentGetDTO;
import org.example.healthcarequalite.dto.incident.IncidentPostDTO;
import org.example.healthcarequalite.dto.incident.IncidentStatusDTO;
import org.example.healthcarequalite.dto.incident.IncidentUpdateDTO;
import org.example.healthcarequalite.dto.statistics.IncidentStatisticsDTO;
import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.entity.Incident;
import org.example.healthcarequalite.entity.User;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentStatus;
import org.example.healthcarequalite.enums.IncidentType;
import org.example.healthcarequalite.enums.Role;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.IncidentMapper;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.example.healthcarequalite.repository.IncidentRepository;
import org.example.healthcarequalite.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {

  private final IncidentRepository incidentRepository;
  private final DepartmentRepository departmentRepository;
  private final UserRepository userRepository;
  private final IncidentMapper incidentMapper;

  public IncidentService(IncidentRepository incidentRepository, DepartmentRepository departmentRepository, UserRepository userRepository, IncidentMapper incidentMapper) {
    this.incidentRepository = incidentRepository;
    this.departmentRepository = departmentRepository;
    this.userRepository = userRepository;
    this.incidentMapper = incidentMapper;
  }

  @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
  public IncidentGetDTO create(IncidentPostDTO incidentPostDTO, String reporterEmail) {
    Department department = departmentRepository.findById(incidentPostDTO.getDepartmentId())
   .orElseThrow(() -> new ResourceNotFoundException("Département introuvable : " + incidentPostDTO.getDepartmentId()));

    User reporter = userRepository.findByEmail(reporterEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));

    Incident incident = incidentMapper.toEntity(incidentPostDTO);

    incident.setStatus(IncidentStatus.OPEN);
    incident.setDepartment(department);
    incident.setReporter(reporter);

    Incident savedIncident = incidentRepository.save(incident);

    return incidentMapper.toGetDTO(savedIncident);
  }

  public List<IncidentGetDTO> findAll() {

    List<Incident> incidents = incidentRepository.findAll();

    return incidents.stream().map(incident -> incidentMapper.toGetDTO(incident)).toList();
  }

  public IncidentGetDTO findById(Long id) {

    Incident incident = findEntityById(id);

    return incidentMapper.toGetDTO(incident);
  }


  public IncidentGetDTO findForStaf(Long id, String connectedEmail) {

    Optional<User> result = userRepository.findByEmail(connectedEmail);

    if (result.isEmpty()) {
      throw new ResourceNotFoundException("Utilisateur introuvable");
    }

    User connectedUser = result.get();
    Incident incident = findEntityById(id);

    if (connectedUser.getRole() == Role.STAFF) {

      Long reporterId = incident.getReporter().getId();
      Long connectedUserId = connectedUser.getId();

      if (!reporterId.equals(connectedUserId)) {
        throw new ResourceNotFoundException("Incident introuvable");
      }
    }

    return incidentMapper.toGetDTO(incident);
  }

  @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
  public IncidentGetDTO update(Long id, IncidentUpdateDTO incidentUpdateDTO) {
    Incident incident = findEntityById(id);

    incidentMapper.updateIncident(incidentUpdateDTO, incident);

    Long departmentId = incidentUpdateDTO.getDepartmentId();

    if (departmentId != null) {

      Department department = departmentRepository.findById(departmentId)
           .orElseThrow(() -> new ResourceNotFoundException("Département introuvable : " + departmentId));

      incident.setDepartment(department);
    }

    Incident updatedIncident = incidentRepository.save(incident);

    return incidentMapper.toGetDTO(updatedIncident);
  }

  @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
  public IncidentGetDTO updateStatus(Long id, IncidentStatusDTO incidentStatusDTO) {
    Incident incident = findEntityById(id);

    incident.setStatus(incidentStatusDTO.getStatus());

    Incident updatedIncident = incidentRepository.save(incident);

    return incidentMapper.toGetDTO(updatedIncident);
  }

  @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
  public void delete(Long id) {

    Incident incident = findEntityById(id);

    incidentRepository.delete(incident);
  }

  private Incident findEntityById(Long id) {

    return incidentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Incident introuvable : " + id));
  }


  public Page<IncidentGetDTO> search( IncidentType type, IncidentGravity gravity, IncidentStatus status,
                                      Long departmentId, String connectedEmail, Pageable pageable) {

    Optional<User> result = userRepository.findByEmail(connectedEmail);

    if (result.isEmpty()) {
      throw new ResourceNotFoundException("Utilisateur introuvable");
    }

    User connectedUser = result.get();

    String reporterEmail = null;

    if (connectedUser.getRole() == Role.STAFF) {
      reporterEmail = connectedUser.getEmail();
    }

    Page<Incident> incidents = incidentRepository.search(
            type, gravity, status, departmentId, reporterEmail, pageable
    );

    List<IncidentGetDTO> incidentDTOs = new ArrayList<>();

    for (Incident incident : incidents.getContent()) {
      IncidentGetDTO dto = incidentMapper.toGetDTO(incident);
      incidentDTOs.add(dto);
    }

    return new PageImpl<>( incidentDTOs, pageable, incidents.getTotalElements()
    );
  }


  public long countCriticalIncidents() {

    long criticalCount = incidentRepository.countByGravity(IncidentGravity.CRITICAL);

    return criticalCount;
  }

  public long countAllIncidents() {

    long total = incidentRepository.count();

    return total;
  }


  public IncidentStatisticsDTO getStatistics() {

    long totalIncidents = countAllIncidents();
    long criticalIncidents = countCriticalIncidents();

    IncidentStatisticsDTO statistics = new IncidentStatisticsDTO();

    statistics.setTotalIncidents(totalIncidents);
    statistics.setCriticalIncidents(criticalIncidents);

    return statistics;
  }

}