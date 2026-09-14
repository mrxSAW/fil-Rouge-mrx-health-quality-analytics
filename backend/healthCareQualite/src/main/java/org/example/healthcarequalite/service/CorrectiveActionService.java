package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionGetDTO;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionPostDTO;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionStatusDTO;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionUpdateDTO;
import org.example.healthcarequalite.entity.CorrectiveAction;
import org.example.healthcarequalite.entity.Incident;
import org.example.healthcarequalite.entity.User;
import org.example.healthcarequalite.enums.CorrectiveActionStatus;
import org.example.healthcarequalite.enums.Role;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.CorrectiveActionMapper;
import org.example.healthcarequalite.repository.CorrectiveActionRepository;
import org.example.healthcarequalite.repository.IncidentRepository;
import org.example.healthcarequalite.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;import org.example.healthcarequalite.dto.user.ResponsibleUserDTO;

@Service
public class CorrectiveActionService {

    private final CorrectiveActionRepository correctiveActionRepository;
    private final IncidentRepository incidentRepository;
    private final UserRepository userRepository;
    private final CorrectiveActionMapper correctiveActionMapper;

    public CorrectiveActionService(CorrectiveActionRepository correctiveActionRepository, IncidentRepository incidentRepository, UserRepository userRepository, CorrectiveActionMapper correctiveActionMapper) {

        this.correctiveActionRepository = correctiveActionRepository;
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
        this.correctiveActionMapper = correctiveActionMapper;
    }

    @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
    public CorrectiveActionGetDTO create(CorrectiveActionPostDTO dto) {

        Optional<Incident> incidentResult = incidentRepository.findById(dto.getIncidentId());

        if (incidentResult.isEmpty()) {
            throw new ResourceNotFoundException("Incident introuvable : " + dto.getIncidentId());
        }

        Optional<User> userResult = userRepository.findById(dto.getResponsibleUserId());

        if (userResult.isEmpty()) {
            throw new ResourceNotFoundException("Responsable introuvable : " + dto.getResponsibleUserId());
        }

        Incident incident = incidentResult.get();
        User responsibleUser = userResult.get();

        CorrectiveAction action = correctiveActionMapper.toEntity(dto);

        action.setIncident(incident);
        action.setResponsibleUser(responsibleUser);
        action.setStatus(CorrectiveActionStatus.TODO);

        CorrectiveAction savedAction = correctiveActionRepository.save(action);

        return correctiveActionMapper.toGetDTO(savedAction);
    }


    public CorrectiveActionGetDTO findById(Long id, String connectedEmail) {

        Optional<User> userResult = userRepository.findByEmail(connectedEmail);

        if (userResult.isEmpty()) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }

        Optional<CorrectiveAction> actionResult = correctiveActionRepository.findById(id);

        if (actionResult.isEmpty()) {
            throw new ResourceNotFoundException("Action corrective introuvable : " + id);
        }

        User connectedUser = userResult.get();
        CorrectiveAction action = actionResult.get();

        if (connectedUser.getRole() == Role.STAFF) {

            Long responsibleUserId = action.getResponsibleUser().getId();
            Long connectedUserId = connectedUser.getId();

            if (!responsibleUserId.equals(connectedUserId)) {
                throw new ResourceNotFoundException("Action corrective introuvable");
            }
        }

        return correctiveActionMapper.toGetDTO(action);
    }

    @Cacheable( cacheNames = "lists", key = "{#root.targetClass.simpleName, #root.methodName, #root.args}", condition = "!T(org.springframework.security.core.context.SecurityContextHolder)" + ".getContext().getAuthentication().getAuthorities()" + ".![authority].contains('ROLE_STAFF')")
    public Page<CorrectiveActionGetDTO> findAll(Long responsibleUserId, String connectedEmail, Pageable pageable) {

        Optional<User> userResult = userRepository.findByEmail(connectedEmail);

        if (userResult.isEmpty()) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }

        User connectedUser = userResult.get();

        if (connectedUser.getRole() == Role.STAFF) {
            responsibleUserId = connectedUser.getId();
        }

        Page<CorrectiveAction> actions;

        if (responsibleUserId == null) {
            actions = correctiveActionRepository.findAll(pageable);
        } else {
            actions = correctiveActionRepository.findByResponsibleUserId(
                    responsibleUserId, pageable
            );
        }

        List<CorrectiveActionGetDTO> actionDTOs = new ArrayList<>();

        for (CorrectiveAction action : actions.getContent()) {
            CorrectiveActionGetDTO dto = correctiveActionMapper.toGetDTO(action);

            actionDTOs.add(dto);
        }

        return new PageImpl<>(
                actionDTOs,
                pageable,
                actions.getTotalElements()
        );
    }

    @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
    public CorrectiveActionGetDTO update( Long id, CorrectiveActionUpdateDTO dto) {

        Optional<CorrectiveAction> actionResult = correctiveActionRepository.findById(id);

        if (actionResult.isEmpty()) {
            throw new ResourceNotFoundException( "Action corrective introuvable : " + id);
        }

        Optional<Incident> incidentResult = incidentRepository.findById(dto.getIncidentId());

        if (incidentResult.isEmpty()) {
            throw new ResourceNotFoundException( "Incident introuvable : " + dto.getIncidentId() );
        }

        Optional<User> userResult = userRepository.findById(dto.getResponsibleUserId());

        if (userResult.isEmpty()) {
            throw new ResourceNotFoundException( "Responsable introuvable : " + dto.getResponsibleUserId() );
        }

        CorrectiveAction action = actionResult.get();
        Incident incident = incidentResult.get();
        User responsibleUser = userResult.get();

        correctiveActionMapper.updateCorrectiveActionFromDTO(dto, action);

        action.setIncident(incident);
        action.setResponsibleUser(responsibleUser);

        CorrectiveAction updatedAction = correctiveActionRepository.save(action);

        return correctiveActionMapper.toGetDTO(updatedAction);
    }



    @CacheEvict(cacheNames = {"statistics", "lists"}, allEntries = true)
    public CorrectiveActionGetDTO updateStatus( Long id, CorrectiveActionStatusDTO dto) {

        Optional<CorrectiveAction> result = correctiveActionRepository.findById(id);

        if (result.isEmpty()) {
            throw new ResourceNotFoundException( "Action corrective introuvable : " + id);
        }

        CorrectiveActionStatus newStatus = dto.getStatus();

        if (newStatus == null) {
            throw new IllegalArgumentException( "Le statut est obligatoire" );
        }

        if (newStatus == CorrectiveActionStatus.OVERDUE) {
            throw new IllegalArgumentException( "Le retard est déterminé automatiquement par la date limite" );
        }

        CorrectiveAction action = result.get();

        action.setStatus(newStatus);

        CorrectiveAction updatedAction = correctiveActionRepository.save(action);

        return correctiveActionMapper.toGetDTO(updatedAction);
    }

    @Cacheable(cacheNames = "lists", key = "{#root.targetClass.simpleName, #root.methodName, #root.args}", condition = "!T(org.springframework.security.core.context.SecurityContextHolder)" + ".getContext().getAuthentication().getAuthorities()" + ".![authority].contains('ROLE_STAFF')")
    public Page<CorrectiveActionGetDTO> findOverdue( Long responsibleUserId, String connectedEmail, Pageable pageable) {

        Optional<User> userResult = userRepository.findByEmail(connectedEmail);

        if (userResult.isEmpty()) {
            throw new ResourceNotFoundException( "Utilisateur introuvable" );
        }

        User connectedUser = userResult.get();

        if (connectedUser.getRole() == Role.STAFF) {
            responsibleUserId = connectedUser.getId();
        }

        List<CorrectiveActionStatus> excludedStatuses = new ArrayList<>();
        excludedStatuses.add(CorrectiveActionStatus.COMPLETED);
        excludedStatuses.add(CorrectiveActionStatus.CANCELLED);

        Page<CorrectiveAction> actions = correctiveActionRepository.findOverdue(LocalDate.now(), excludedStatuses, responsibleUserId, pageable);

        List<CorrectiveActionGetDTO> actionDTOs = new ArrayList<>();

        for (CorrectiveAction action : actions.getContent()) {
            CorrectiveActionGetDTO dto = correctiveActionMapper.toGetDTO(action);

            actionDTOs.add(dto);
        }

        return new PageImpl<>( actionDTOs, pageable, actions.getTotalElements() );
    }


    public long countOverdueActions() {

        List<CorrectiveActionStatus> excludedStatuses = new ArrayList<>();

        excludedStatuses.add(CorrectiveActionStatus.COMPLETED);
        excludedStatuses.add(CorrectiveActionStatus.CANCELLED);

        long total = correctiveActionRepository.countOverdue( LocalDate.now(), excludedStatuses );

        return total;
    }


    public Page<ResponsibleUserDTO> findResponsibleUsers(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        List<ResponsibleUserDTO> userDTOs = new ArrayList<>();

        for (User user : users.getContent()) {

            ResponsibleUserDTO dto = new ResponsibleUserDTO();

            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());

            userDTOs.add(dto);
        }

        return new PageImpl<>(
                userDTOs,
                pageable,
                users.getTotalElements()
        );
    }

}