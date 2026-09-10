package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.statistics.ConformityStatisticsDTO;
import org.example.healthcarequalite.dto.statistics.RiskStatisticsDTO;
import org.example.healthcarequalite.repository.AuditRepository;
import org.example.healthcarequalite.repository.CorrectiveActionRepository;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.example.healthcarequalite.repository.IncidentRepository;
import org.example.healthcarequalite.repository.MonthlyAdmissionsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;import static org.junit.jupiter.api.Assertions.assertNull;import org.example.healthcarequalite.dto.statistics.QualityScoreDTO;import org.example.healthcarequalite.dto.statistics.IncidentRateDTO;
import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.entity.MonthlyAdmissions;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private IncidentService incidentService;

    @Mock
    private AuditService auditService;

    @Mock
    private CorrectiveActionService correctiveActionService;

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CorrectiveActionRepository correctiveActionRepository;

    @Mock
    private MonthlyAdmissionsRepository monthlyAdmissionsRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void getRiskStatisticsShouldCalculateScore() {

        when(incidentService.countCriticalIncidents()).thenReturn(3L);
        when(auditService.countNonCompliantCriteria()).thenReturn(9L);
        when(correctiveActionService.countOverdueActions()).thenReturn(2L);

        RiskStatisticsDTO result =
                statisticsService.getRiskStatistics();

        assertEquals(3L, result.getCriticalIncidents());
        assertEquals(9L, result.getNonCompliantCriteria());
        assertEquals(2L, result.getOverdueActions());
        assertEquals(4.5, result.getRiskScore(), 0.001);
    }

    @Test
    void getConformityStatisticsShouldCalculateRate() {

        when(auditRepository.sumTotalCriteria()).thenReturn(40L);
        when(auditRepository.sumCompliantCriteria()).thenReturn(30L);

        ConformityStatisticsDTO result =
                statisticsService.getConformityStatistics();

        assertEquals(40L, result.getTotalCriteria());
        assertEquals(30L, result.getCompliantCriteria());
        assertEquals(75.0, result.getConformityRate(), 0.001);
    }

    @Test
    void getConformityStatisticsShouldReturnNullWhenNoCriteria() {

        when(auditRepository.sumTotalCriteria()).thenReturn(0L);
        when(auditRepository.sumCompliantCriteria()).thenReturn(0L);

        ConformityStatisticsDTO result = statisticsService.getConformityStatistics();

        assertEquals(0L, result.getTotalCriteria());
        assertEquals(0L, result.getCompliantCriteria());
        assertNull(result.getConformityRate());
    }

    @Test
    void getQualityScoreShouldReturnNullWhenNoAuditScore() {

        when(auditRepository.calculateAverageScore()).thenReturn(null);

        QualityScoreDTO result = statisticsService.getQualityScore();

        assertNull(result.getQualityScore());
    }



    @Test
    void getQualityScoreShouldRoundToTwoDecimals() {

        when(auditRepository.calculateAverageScore()).thenReturn(83.333333333);

        QualityScoreDTO result = statisticsService.getQualityScore();

        assertEquals(83.33, result.getQualityScore(), 0.001);
    }


    @Test
    void getIncidentRateShouldCalculateRateForRequestedMonth() {

        Department department = new Department();
        department.setId(1L);
        department.setName("CARDIOLOGIE");

        MonthlyAdmissions admissions = new MonthlyAdmissions();
        admissions.setAdmissionCount(500);

        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 1);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        when(incidentRepository.countIncidentsForPeriod(1L, startDate, endDate)).thenReturn(5L);

        when(monthlyAdmissionsRepository.findByDepartmentIdAndYearAndMonth(1L, 2026, 9)).thenReturn(Optional.of(admissions));

        IncidentRateDTO result = statisticsService.getIncidentRate(1L, 2026, 9);

        assertEquals(5L, result.getTotalIncidents());
        assertEquals(Integer.valueOf(500), result.getAdmissionCount());
        assertEquals(10.0, result.getIncidentsPerThousandAdmissions(), 0.001);

        verify(incidentRepository).countIncidentsForPeriod(1L, startDate, endDate);
    }



    @Test
    void getIncidentRateShouldReturnNullWhenAdmissionsAreMissing() {

        Department department = new Department();
        department.setId(1L);
        department.setName("CARDIOLOGIE");

        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 1);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        when(incidentRepository.countIncidentsForPeriod(1L, startDate, endDate)).thenReturn(5L);

        when(monthlyAdmissionsRepository.findByDepartmentIdAndYearAndMonth(1L, 2026, 9)).thenReturn(Optional.empty());

        IncidentRateDTO result = statisticsService.getIncidentRate(1L, 2026, 9);

        assertEquals(5L, result.getTotalIncidents());
        assertNull(result.getAdmissionCount());
        assertNull(result.getIncidentsPerThousandAdmissions());
    }

    @Test
    void getIncidentRateShouldReturnNullWhenAdmissionsAreZero() {

        Department department = new Department();
        department.setId(1L);
        department.setName("CARDIOLOGIE");

        MonthlyAdmissions admissions = new MonthlyAdmissions();
        admissions.setAdmissionCount(0);

        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 1);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        when(incidentRepository.countIncidentsForPeriod(1L, startDate, endDate)).thenReturn(5L);

        when(monthlyAdmissionsRepository.findByDepartmentIdAndYearAndMonth(1L, 2026, 9)).thenReturn(Optional.of(admissions));

        IncidentRateDTO result = statisticsService.getIncidentRate(1L, 2026, 9);

        assertEquals(5L, result.getTotalIncidents());
        assertEquals(Integer.valueOf(0), result.getAdmissionCount());
        assertNull(result.getIncidentsPerThousandAdmissions());
    }

}