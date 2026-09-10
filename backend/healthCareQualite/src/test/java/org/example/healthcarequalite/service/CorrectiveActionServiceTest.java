package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionGetDTO;
import org.example.healthcarequalite.dto.correctiveAction.CorrectiveActionStatusDTO;
import org.example.healthcarequalite.entity.CorrectiveAction;
import org.example.healthcarequalite.entity.User;
import org.example.healthcarequalite.enums.CorrectiveActionStatus;
import org.example.healthcarequalite.enums.Role;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.CorrectiveActionMapper;
import org.example.healthcarequalite.repository.CorrectiveActionRepository;
import org.example.healthcarequalite.repository.IncidentRepository;
import org.example.healthcarequalite.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrectiveActionServiceTest {

    @Mock
    private CorrectiveActionRepository correctiveActionRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CorrectiveActionMapper correctiveActionMapper;

    @InjectMocks
    private CorrectiveActionService correctiveActionService;

    @Test
    void staffShouldNotReadAnotherUsersAction() {

        User staff = new User();
        staff.setId(1L);
        staff.setEmail("staff@test.com");
        staff.setRole(Role.STAFF);

        User responsibleUser = new User();
        responsibleUser.setId(2L);

        CorrectiveAction action = new CorrectiveAction();
        action.setId(10L);
        action.setResponsibleUser(responsibleUser);

        when(userRepository.findByEmail("staff@test.com"))
                .thenReturn(Optional.of(staff));

        when(correctiveActionRepository.findById(10L))
                .thenReturn(Optional.of(action));

        assertThrows(
                ResourceNotFoundException.class,
                () -> correctiveActionService.findById(
                        10L, "staff@test.com"
                )
        );

        verifyNoInteractions(correctiveActionMapper);
    }

    @Test
    void staffShouldReadOwnAction() {

        User staff = new User();
        staff.setId(1L);
        staff.setEmail("staff@test.com");
        staff.setRole(Role.STAFF);

        CorrectiveAction action = new CorrectiveAction();
        action.setId(10L);
        action.setResponsibleUser(staff);

        CorrectiveActionGetDTO expectedDTO = new CorrectiveActionGetDTO();
        expectedDTO.setId(10L);

        when(userRepository.findByEmail("staff@test.com"))
                .thenReturn(Optional.of(staff));

        when(correctiveActionRepository.findById(10L))
                .thenReturn(Optional.of(action));

        when(correctiveActionMapper.toGetDTO(action))
                .thenReturn(expectedDTO);

        CorrectiveActionGetDTO result =
                correctiveActionService.findById(10L, "staff@test.com");

        assertSame(expectedDTO, result);
    }

    @Test
    void adminShouldReadAnotherUsersAction() {

        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        User responsibleUser = new User();
        responsibleUser.setId(2L);

        CorrectiveAction action = new CorrectiveAction();
        action.setId(10L);
        action.setResponsibleUser(responsibleUser);

        CorrectiveActionGetDTO expectedDTO = new CorrectiveActionGetDTO();
        expectedDTO.setId(10L);

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(admin));

        when(correctiveActionRepository.findById(10L))
                .thenReturn(Optional.of(action));

        when(correctiveActionMapper.toGetDTO(action))
                .thenReturn(expectedDTO);

        CorrectiveActionGetDTO result =
                correctiveActionService.findById(10L, "admin@test.com");

        assertSame(expectedDTO, result);
    }

    @Test
    void findByIdShouldRejectMissingAction() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(admin));

        when(correctiveActionRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> correctiveActionService.findById(
                        999L, "admin@test.com"
                )
        );

        verifyNoInteractions(correctiveActionMapper);
    }

    @Test
    void staffShouldNotOverrideResponsibleUserFilter() {

        User staff = new User();
        staff.setId(1L);
        staff.setRole(Role.STAFF);

        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findByEmail("staff@test.com"))
                .thenReturn(Optional.of(staff));

        when(correctiveActionRepository.findByResponsibleUserId(
                1L, pageable))
                .thenReturn(Page.empty(pageable));

        Page<CorrectiveActionGetDTO> result =
                correctiveActionService.findAll(
                        2L, "staff@test.com", pageable
                );

        assertTrue(result.isEmpty());

        verify(correctiveActionRepository)
                .findByResponsibleUserId(1L, pageable);

        verify(correctiveActionRepository, never())
                .findByResponsibleUserId(2L, pageable);

        verify(correctiveActionRepository, never())
                .findAll(any(Pageable.class));
    }

    @Test
    void adminShouldUseRequestedResponsibleUserFilter() {

        User admin = new User();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(admin));

        when(correctiveActionRepository.findByResponsibleUserId(
                2L, pageable))
                .thenReturn(Page.empty(pageable));

        correctiveActionService.findAll(
                2L, "admin@test.com", pageable
        );

        verify(correctiveActionRepository)
                .findByResponsibleUserId(2L, pageable);
    }

    @Test
    void staffOverdueSearchShouldUseOwnId() {

        User staff = new User();
        staff.setId(1L);
        staff.setRole(Role.STAFF);

        Pageable pageable = PageRequest.of(0, 5);

        List<CorrectiveActionStatus> excludedStatuses = List.of(
                CorrectiveActionStatus.COMPLETED,
                CorrectiveActionStatus.CANCELLED
        );

        when(userRepository.findByEmail("staff@test.com"))
                .thenReturn(Optional.of(staff));

        when(correctiveActionRepository.findOverdue(
                any(LocalDate.class),
                eq(excludedStatuses),
                eq(1L),
                eq(pageable)))
                .thenReturn(Page.empty(pageable));

        correctiveActionService.findOverdue(
                2L, "staff@test.com", pageable
        );

        verify(correctiveActionRepository).findOverdue(
                any(LocalDate.class),
                eq(excludedStatuses),
                eq(1L),
                eq(pageable)
        );
    }

    @Test
    void updateStatusShouldCompleteAction() {

        CorrectiveAction action = new CorrectiveAction();
        action.setId(10L);
        action.setStatus(CorrectiveActionStatus.IN_PROGRESS);

        CorrectiveActionStatusDTO dto = new CorrectiveActionStatusDTO();
        dto.setStatus(CorrectiveActionStatus.COMPLETED);

        CorrectiveActionGetDTO expectedDTO = new CorrectiveActionGetDTO();
        expectedDTO.setId(10L);
        expectedDTO.setStatus(CorrectiveActionStatus.COMPLETED);

        when(correctiveActionRepository.findById(10L))
                .thenReturn(Optional.of(action));

        when(correctiveActionRepository.save(action))
                .thenReturn(action);

        when(correctiveActionMapper.toGetDTO(action))
                .thenReturn(expectedDTO);

        CorrectiveActionGetDTO result =
                correctiveActionService.updateStatus(10L, dto);

        assertEquals(
                CorrectiveActionStatus.COMPLETED,
                action.getStatus()
        );

        assertSame(expectedDTO, result);

        verify(correctiveActionRepository).save(action);
    }

    @Test
    void updateStatusShouldRejectManualOverdueStatus() {

        CorrectiveAction action = new CorrectiveAction();
        action.setId(10L);
        action.setStatus(CorrectiveActionStatus.TODO);

        CorrectiveActionStatusDTO dto = new CorrectiveActionStatusDTO();
        dto.setStatus(CorrectiveActionStatus.OVERDUE);

        when(correctiveActionRepository.findById(10L))
                .thenReturn(Optional.of(action));

        assertThrows(
                IllegalArgumentException.class,
                () -> correctiveActionService.updateStatus(10L, dto)
        );

        assertEquals(CorrectiveActionStatus.TODO, action.getStatus());

        verify(correctiveActionRepository, never())
                .save(any(CorrectiveAction.class));
    }

    @Test
    void updateStatusShouldRejectNullStatus() {

        CorrectiveAction action = new CorrectiveAction();
        action.setId(10L);
        action.setStatus(CorrectiveActionStatus.TODO);

        CorrectiveActionStatusDTO dto = new CorrectiveActionStatusDTO();

        when(correctiveActionRepository.findById(10L))
                .thenReturn(Optional.of(action));

        assertThrows(
                IllegalArgumentException.class,
                () -> correctiveActionService.updateStatus(10L, dto)
        );

        verify(correctiveActionRepository, never())
                .save(any(CorrectiveAction.class));
    }

    @Test
    void countOverdueShouldExcludeCompletedAndCancelledActions() {

        List<CorrectiveActionStatus> excludedStatuses = List.of(
                CorrectiveActionStatus.COMPLETED,
                CorrectiveActionStatus.CANCELLED
        );

        when(correctiveActionRepository.countOverdue(
                any(LocalDate.class),
                eq(excludedStatuses)))
                .thenReturn(3L);

        long result = correctiveActionService.countOverdueActions();

        assertEquals(3L, result);

        verify(correctiveActionRepository).countOverdue(
                any(LocalDate.class),
                eq(excludedStatuses)
        );
    }




}