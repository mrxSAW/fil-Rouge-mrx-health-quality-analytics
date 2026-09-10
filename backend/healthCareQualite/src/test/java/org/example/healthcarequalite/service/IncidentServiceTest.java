package org.example.healthcarequalite.service;

import org.example.healthcarequalite.entity.Incident;
import org.example.healthcarequalite.entity.User;
import org.example.healthcarequalite.enums.Role;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.IncidentMapper;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.example.healthcarequalite.repository.IncidentRepository;
import org.example.healthcarequalite.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;import org.example.healthcarequalite.dto.incident.IncidentGetDTO;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IncidentMapper incidentMapper;

    @InjectMocks
    private IncidentService incidentService;

    @Test
    void staffShouldNotReadAnotherUsersIncident() {

        User connectedUser = new User();
        connectedUser.setId(1L);
        connectedUser.setEmail("staff@test.com");
        connectedUser.setRole(Role.STAFF);

        User reporter = new User();
        reporter.setId(2L);

        Incident incident = new Incident();
        incident.setId(10L);
        incident.setReporter(reporter);

        when(userRepository.findByEmail("staff@test.com")).thenReturn(Optional.of(connectedUser));

        when(incidentRepository.findById(10L)).thenReturn(Optional.of(incident));

        assertThrows(ResourceNotFoundException.class, () -> incidentService.findForStaf(10L, "staff@test.com"));

        verifyNoInteractions(incidentMapper);
    }



    @Test
    void staffShouldReadOwnIncident() {

        User connectedUser = new User();
        connectedUser.setId(1L);
        connectedUser.setEmail("staff@test.com");
        connectedUser.setRole(Role.STAFF);

        Incident incident = new Incident();
        incident.setId(10L);
        incident.setReporter(connectedUser);

        IncidentGetDTO expectedDTO = new IncidentGetDTO();
        expectedDTO.setId(10L);

        when(userRepository.findByEmail("staff@test.com")).thenReturn(Optional.of(connectedUser));

        when(incidentRepository.findById(10L)).thenReturn(Optional.of(incident));

        when(incidentMapper.toGetDTO(incident)).thenReturn(expectedDTO);

        IncidentGetDTO result = incidentService.findForStaf(10L, "staff@test.com");

        assertSame(expectedDTO, result);

        verify(incidentMapper).toGetDTO(incident);
    }


    @Test
    void adminShouldReadAnotherUsersIncident() {

        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        User reporter = new User();
        reporter.setId(2L);

        Incident incident = new Incident();
        incident.setId(10L);
        incident.setReporter(reporter);

        IncidentGetDTO expectedDTO = new IncidentGetDTO();
        expectedDTO.setId(10L);

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        when(incidentRepository.findById(10L)).thenReturn(Optional.of(incident));

        when(incidentMapper.toGetDTO(incident)).thenReturn(expectedDTO);

        IncidentGetDTO result = incidentService.findForStaf(10L, "admin@test.com");

        assertSame(expectedDTO, result);

        verify(incidentMapper).toGetDTO(incident);
    }


    @Test
    void staffSearchShouldUseConnectedEmail() {

        User staff = new User();
        staff.setId(1L);
        staff.setEmail("staff@test.com");
        staff.setRole(Role.STAFF);

        Pageable pageable = PageRequest.of(0, 5);

        Incident incident = new Incident();
        incident.setId(10L);
        incident.setReporter(staff);

        Page<Incident> incidents = new PageImpl<>(List.of(incident), pageable, 1);

        IncidentGetDTO expectedDTO = new IncidentGetDTO();
        expectedDTO.setId(10L);

        when(userRepository.findByEmail("staff@test.com")).thenReturn(Optional.of(staff));

        when(incidentRepository.search(null, null, null, null, "staff@test.com", pageable)).thenReturn(incidents);

        when(incidentMapper.toGetDTO(incident)).thenReturn(expectedDTO);

        Page<IncidentGetDTO> result = incidentService.search(null, null, null, null, "staff@test.com", pageable);

        assertEquals(1L, result.getTotalElements());
        assertSame(expectedDTO, result.getContent().get(0));

        verify(incidentRepository).search(null, null, null, null, "staff@test.com", pageable);
    }







}