package org.example.healthcarequalite.repository;

import org.example.healthcarequalite.entity.CorrectiveAction;
import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.entity.Incident;
import org.example.healthcarequalite.entity.User;
import org.example.healthcarequalite.enums.CorrectiveActionStatus;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentStatus;
import org.example.healthcarequalite.enums.IncidentType;
import org.example.healthcarequalite.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class CorrectiveActionRepositoryTest {

    @Autowired
    private CorrectiveActionRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindOnlyUnfinishedActionsBeforeToday() {

        LocalDate today = LocalDate.of(2026, 9, 9);

        Department department = new Department();
        department.setName("CARDIOLOGIE");
        entityManager.persist(department);

        User responsible = new User();
        responsible.setFirstName("Ali");
        responsible.setLastName("Test");
        responsible.setUsername("ali_test");
        responsible.setEmail("ali@test.com");
        responsible.setPassword("test-password");
        responsible.setRole(Role.STAFF);
        entityManager.persist(responsible);

        Incident incident = new Incident();
        incident.setTitle("Incident de test");
        incident.setDescription("Description de test");
        incident.setType(IncidentType.MEDICAL);
        incident.setGravity(IncidentGravity.HIGH);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setIncidentDate(today.minusDays(5));
        incident.setDepartment(department);
        incident.setReporter(responsible);
        entityManager.persist(incident);

        CorrectiveAction overdueAction = createAction(
                incident,
                responsible,
                today.minusDays(1),
                CorrectiveActionStatus.IN_PROGRESS
        );

        createAction(
                incident,
                responsible,
                today.minusDays(1),
                CorrectiveActionStatus.COMPLETED
        );

        createAction(
                incident,
                responsible,
                today.minusDays(1),
                CorrectiveActionStatus.CANCELLED
        );

        createAction(
                incident,
                responsible,
                today,
                CorrectiveActionStatus.TODO
        );

        createAction(
                incident,
                responsible,
                today.plusDays(1),
                CorrectiveActionStatus.TODO
        );

        entityManager.flush();
        entityManager.clear();

        List<CorrectiveActionStatus> excludedStatuses = List.of(
                CorrectiveActionStatus.COMPLETED,
                CorrectiveActionStatus.CANCELLED
        );

        Page<CorrectiveAction> result = repository.findOverdue(
                today,
                excludedStatuses,
                null,
                PageRequest.of(0, 10)
        );

        assertEquals(1L, result.getTotalElements());
        assertEquals(
                overdueAction.getId(),
                result.getContent().get(0).getId()
        );

        long count = repository.countOverdue(
                today,
                excludedStatuses
        );

        assertEquals(1L, count);
    }

    private CorrectiveAction createAction(Incident incident, User responsible,
            LocalDate deadline, CorrectiveActionStatus status) {

        CorrectiveAction action = new CorrectiveAction();
        action.setTitle("Action de test");
        action.setDescription("Description de l'action");
        action.setDeadline(deadline);
        action.setStatus(status);
        action.setIncident(incident);
        action.setResponsibleUser(responsible);

        entityManager.persist(action);

        return action;
    }



    @Test
    void shouldFindOverdueActionsForRequestedResponsibleUser() {

        LocalDate today = LocalDate.of(2026, 9, 9);

        Department department = new Department();
        department.setName("URGENCES");
        entityManager.persist(department);

        User firstUser = new User();
        firstUser.setFirstName("Ali");
        firstUser.setLastName("Test");
        firstUser.setUsername("ali_test");
        firstUser.setEmail("ali@test.com");
        firstUser.setPassword("test-password");
        firstUser.setRole(Role.STAFF);
        entityManager.persist(firstUser);

        User secondUser = new User();
        secondUser.setFirstName("Sara");
        secondUser.setLastName("Test");
        secondUser.setUsername("sara_test");
        secondUser.setEmail("sara@test.com");
        secondUser.setPassword("test-password");
        secondUser.setRole(Role.STAFF);
        entityManager.persist(secondUser);

        Incident incident = new Incident();
        incident.setTitle("Incident de test");
        incident.setDescription("Description de test");
        incident.setType(IncidentType.MEDICAL);
        incident.setGravity(IncidentGravity.HIGH);
        incident.setStatus(IncidentStatus.OPEN);
        incident.setIncidentDate(today.minusDays(5));
        incident.setDepartment(department);
        incident.setReporter(firstUser);
        entityManager.persist(incident);

        CorrectiveAction expectedAction = createAction(
                incident,
                firstUser,
                today.minusDays(1),
                CorrectiveActionStatus.TODO
        );

        createAction(
                incident,
                secondUser,
                today.minusDays(1),
                CorrectiveActionStatus.IN_PROGRESS
        );

        Long firstUserId = firstUser.getId();
        long expectedActionId = expectedAction.getId();

        entityManager.flush();
        entityManager.clear();

        List<CorrectiveActionStatus> excludedStatuses = List.of(
                CorrectiveActionStatus.COMPLETED,
                CorrectiveActionStatus.CANCELLED
        );

        Page<CorrectiveAction> result = repository.findOverdue(
                today,
                excludedStatuses,
                firstUserId,
                PageRequest.of(0, 10)
        );

        assertEquals(1L, result.getTotalElements());
        assertEquals(
                expectedActionId,
                result.getContent().get(0).getId()
        );
    }




    @Test
    void shouldCountOverdueActionsForRequestedDepartment() {

        LocalDate today = LocalDate.of(2026, 9, 9);

        Department cardiology = new Department();
        cardiology.setName("CARDIOLOGIE");
        entityManager.persist(cardiology);

        Department emergency = new Department();
        emergency.setName("URGENCES");
        entityManager.persist(emergency);

        User responsible = new User();
        responsible.setFirstName("Ali");
        responsible.setLastName("Test");
        responsible.setUsername("ali_test");
        responsible.setEmail("ali@test.com");
        responsible.setPassword("test-password");
        responsible.setRole(Role.STAFF);
        entityManager.persist(responsible);

        Incident firstIncident = new Incident();
        firstIncident.setTitle("Incident cardiologie");
        firstIncident.setDescription("Description de test");
        firstIncident.setType(IncidentType.MEDICAL);
        firstIncident.setGravity(IncidentGravity.HIGH);
        firstIncident.setStatus(IncidentStatus.OPEN);
        firstIncident.setIncidentDate(today.minusDays(5));
        firstIncident.setDepartment(cardiology);
        firstIncident.setReporter(responsible);
        entityManager.persist(firstIncident);

        Incident secondIncident = new Incident();
        secondIncident.setTitle("Incident urgences");
        secondIncident.setDescription("Description de test");
        secondIncident.setType(IncidentType.MEDICAL);
        secondIncident.setGravity(IncidentGravity.HIGH);
        secondIncident.setStatus(IncidentStatus.OPEN);
        secondIncident.setIncidentDate(today.minusDays(5));
        secondIncident.setDepartment(emergency);
        secondIncident.setReporter(responsible);
        entityManager.persist(secondIncident);

        createAction(
                firstIncident,
                responsible,
                today.minusDays(1),
                CorrectiveActionStatus.TODO
        );

        createAction(
                firstIncident,
                responsible,
                today.minusDays(1),
                CorrectiveActionStatus.COMPLETED
        );

        createAction(
                secondIncident,
                responsible,
                today.minusDays(1),
                CorrectiveActionStatus.IN_PROGRESS
        );

        Long departmentId = cardiology.getId();

        entityManager.flush();
        entityManager.clear();

        List<CorrectiveActionStatus> excludedStatuses = List.of(
                CorrectiveActionStatus.COMPLETED,
                CorrectiveActionStatus.CANCELLED
        );

        long result = repository.countOverdueByDepartment(
                departmentId,
                today,
                excludedStatuses
        );

        assertEquals(1L, result);
    }





}