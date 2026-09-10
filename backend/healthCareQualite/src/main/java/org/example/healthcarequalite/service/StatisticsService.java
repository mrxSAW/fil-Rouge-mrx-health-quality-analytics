package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.statistics.*;
import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.entity.MonthlyAdmissions;
import org.example.healthcarequalite.enums.CorrectiveActionStatus;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentType;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StatisticsService {

    private final IncidentService incidentService;
    private final AuditService auditService;
    private final CorrectiveActionService correctiveActionService;
    private final AuditRepository auditRepository;
    private final IncidentRepository incidentRepository;
    private final DepartmentRepository departmentRepository;
    private final CorrectiveActionRepository correctiveActionRepository;
    private final MonthlyAdmissionsRepository monthlyAdmissionsRepository;

    public StatisticsService( IncidentService incidentService, AuditService auditService,MonthlyAdmissionsRepository monthlyAdmissionsRepository, CorrectiveActionService correctiveActionService, AuditRepository auditRepository, IncidentRepository incidentRepository, DepartmentRepository departmentRepository, CorrectiveActionRepository correctiveActionRepository) {

        this.incidentService = incidentService;
        this.auditService = auditService;
        this.correctiveActionService = correctiveActionService;
        this.auditRepository = auditRepository;
        this.incidentRepository = incidentRepository;
        this.departmentRepository = departmentRepository;
        this.correctiveActionRepository = correctiveActionRepository;
        this.monthlyAdmissionsRepository = monthlyAdmissionsRepository;
    }


    public RiskStatisticsDTO getRiskStatistics() {

        long criticalIncidents = incidentService.countCriticalIncidents();

        long nonCompliantCriteria = auditService.countNonCompliantCriteria();

        long overdueActions = correctiveActionService.countOverdueActions();

        double riskScore = 0.4 * criticalIncidents + 0.3 * nonCompliantCriteria + 0.3 * overdueActions;

        riskScore = Math.round(riskScore * 100.0) / 100.0;

        RiskStatisticsDTO statistics = new RiskStatisticsDTO();

        statistics.setCriticalIncidents(criticalIncidents);
        statistics.setNonCompliantCriteria(nonCompliantCriteria);
        statistics.setOverdueActions(overdueActions);
        statistics.setRiskScore(riskScore);

        return statistics;
    }

    public ConformityStatisticsDTO getConformityStatistics() {

        long totalCriteria = auditRepository.sumTotalCriteria();
        long compliantCriteria = auditRepository.sumCompliantCriteria();

        Double conformityRate = null;

        if (totalCriteria > 0) {
            double rate = compliantCriteria * 100.0 / totalCriteria;

            conformityRate = Math.round(rate * 100.0) / 100.0;
        }

        ConformityStatisticsDTO statistics = new ConformityStatisticsDTO();

        statistics.setTotalCriteria(totalCriteria);
        statistics.setCompliantCriteria(compliantCriteria);
        statistics.setConformityRate(conformityRate);

        return statistics;
    }


    public List<MonthlyIncidentDTO> getMonthlyIncidents() {

        List<MonthlyIncidentDTO> statistics = incidentRepository.countIncidentsByMonth();

        return statistics;
    }


    public List<IncidentGravityStatisticsDTO> getIncidentsByGravity() {

        List<IncidentGravityStatisticsDTO> statistics = new ArrayList<>();

        for (IncidentGravity gravity : IncidentGravity.values()) {

            long total = incidentRepository.countByGravity(gravity);

            IncidentGravityStatisticsDTO dto = new IncidentGravityStatisticsDTO();

            dto.setGravity(gravity);
            dto.setTotalIncidents(total);

            statistics.add(dto);
        }

        return statistics;
    }


    public List<IncidentTypeStatisticsDTO> getIncidentsByType() {

        List<IncidentTypeStatisticsDTO> statistics = new ArrayList<>();

        for (IncidentType type : IncidentType.values()) {

            long total = incidentRepository.countByType(type);

            IncidentTypeStatisticsDTO dto = new IncidentTypeStatisticsDTO();

            dto.setType(type);
            dto.setTotalIncidents(total);

            statistics.add(dto);
        }

        return statistics;
    }


    public List<DepartmentConformityDTO> getConformityByDepartment() {

        List<Department> departments = departmentRepository.findAll();

        List<DepartmentConformityDTO> statistics = new ArrayList<>();

        for (Department department : departments) {

            Long departmentId = department.getId();

            long totalCriteria = auditRepository.sumTotalCriteriaByDepartment(departmentId);

            long compliantCriteria = auditRepository.sumCompliantCriteriaByDepartment(departmentId);

            Double conformityRate = null;

            if (totalCriteria > 0) {
                double rate = compliantCriteria * 100.0 / totalCriteria;

                conformityRate = Math.round(rate * 100.0) / 100.0;
            }

            DepartmentConformityDTO dto = new DepartmentConformityDTO();

            dto.setDepartmentId(departmentId);
            dto.setDepartmentName(department.getName());
            dto.setTotalCriteria(totalCriteria);
            dto.setCompliantCriteria(compliantCriteria);
            dto.setConformityRate(conformityRate);

            statistics.add(dto);
        }

        return statistics;
    }



    public List<DepartmentRiskDTO> getRiskByDepartment() {

        List<Department> departments = departmentRepository.findAll();

        List<DepartmentRiskDTO> statistics = new ArrayList<>();

        List<CorrectiveActionStatus> excludedStatuses = new ArrayList<>();
        excludedStatuses.add(CorrectiveActionStatus.COMPLETED);
        excludedStatuses.add(CorrectiveActionStatus.CANCELLED);

        LocalDate today = LocalDate.now();

        for (Department department : departments) {

            Long departmentId = department.getId();

          long criticalIncidents = incidentRepository.countByGravityAndDepartmentId(IncidentGravity.CRITICAL, departmentId );

            long nonCompliantCriteria = auditRepository.countNonCompliantCriteriaByDepartment( departmentId );

            long overdueActions = correctiveActionRepository.countOverdueByDepartment(departmentId, today, excludedStatuses );

            double riskScore = (0.4 * criticalIncidents) + (0.3 * nonCompliantCriteria) + (0.3 * overdueActions) ;

            riskScore = Math.round(riskScore * 100.0) / 100.0;

            DepartmentRiskDTO dto = new DepartmentRiskDTO();

            dto.setDepartmentId(departmentId);
            dto.setDepartmentName(department.getName());
            dto.setCriticalIncidents(criticalIncidents);
            dto.setNonCompliantCriteria(nonCompliantCriteria);
            dto.setOverdueActions(overdueActions);
            dto.setRiskScore(riskScore);

            statistics.add(dto);
        }

        return statistics;
    }


    public IncidentRateDTO getIncidentRate( Long departmentId, Integer year, Integer month) {

        if (departmentId == null || departmentId <= 0) {
            throw new IllegalArgumentException( "L'identifiant du département doit être positif" );
        }

        if (year == null || year < 1 || year > 9999) {
            throw new IllegalArgumentException( "L'année doit être comprise entre 1 et 9999" );
        }

        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException( "Le mois doit être compris entre 1 et 12" );
        }

        Optional<Department> departmentResult =  departmentRepository.findById(departmentId);

        if (departmentResult.isEmpty()) {
            throw new ResourceNotFoundException( "Département introuvable : " + departmentId );
        }

        Department department = departmentResult.get();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        long totalIncidents = incidentRepository.countIncidentsForPeriod( departmentId, startDate, endDate );

        Optional<MonthlyAdmissions> admissionsResult =
                monthlyAdmissionsRepository.findByDepartmentIdAndYearAndMonth( departmentId, year, month );

        Integer admissionCount = null;
        Double rate = null;

        if (admissionsResult.isPresent()) {

            MonthlyAdmissions admissions = admissionsResult.get();
            admissionCount = admissions.getAdmissionCount();

            if (admissionCount != null && admissionCount > 0) {
                double calculatedRate = totalIncidents * 1000.0 / admissionCount;

                rate = Math.round(calculatedRate * 100.0) / 100.0;
            }
        }

        IncidentRateDTO statistics = new IncidentRateDTO();

        statistics.setDepartmentId(departmentId);
        statistics.setDepartmentName(department.getName());
        statistics.setYear(year);
        statistics.setMonth(month);
        statistics.setTotalIncidents(totalIncidents);
        statistics.setAdmissionCount(admissionCount);
        statistics.setIncidentsPerThousandAdmissions(rate);

        return statistics;
    }


    public QualityScoreDTO getQualityScore() {

        Double averageScore = auditRepository.calculateAverageScore();

        if (averageScore != null) {
            averageScore = Math.round(averageScore * 100.0) / 100.0;
        }

        QualityScoreDTO statistics = new QualityScoreDTO();

        statistics.setQualityScore(averageScore);

        return statistics;
    }


    public DashboardDTO getDashboard() {

        long totalIncidents = incidentService.countAllIncidents();
        long totalAudits = auditRepository.count();

        RiskStatisticsDTO risk = getRiskStatistics();
        ConformityStatisticsDTO conformity = getConformityStatistics();
        QualityScoreDTO quality = getQualityScore();

        DashboardDTO dashboard = new DashboardDTO();

        dashboard.setTotalIncidents(totalIncidents);
        dashboard.setTotalAudits(totalAudits);
        dashboard.setCriticalIncidents(risk.getCriticalIncidents());
        dashboard.setOverdueActions(risk.getOverdueActions());
        dashboard.setConformityRate(conformity.getConformityRate());
        dashboard.setQualityScore(quality.getQualityScore());
        dashboard.setRiskScore(risk.getRiskScore());

        return dashboard;
    }


    public MonthlyQualityReportDTO getMonthlyQualityReport(Integer year, Integer month) {

        if (year == null || year < 1 || year > 9999) {
            throw new IllegalArgumentException( "L'année doit être comprise entre 1 et 9999");
        }

        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException( "Le mois doit être compris entre 1 et 12" );
        }

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        long totalIncidents = incidentRepository.countForMonthlyReport( startDate, endDate );

        long criticalIncidents = incidentRepository.countByGravityForMonthlyReport(startDate, endDate, IncidentGravity.CRITICAL);

        long totalAudits = auditRepository.countForMonthlyReport( startDate, endDate );

        long totalCriteria = auditRepository.sumTotalCriteriaForMonthlyReport( startDate, endDate );

        long compliantCriteria = auditRepository.sumCompliantCriteriaForMonthlyReport( startDate, endDate );

        Double conformityRate = null;

        if (totalCriteria > 0) {
            double rate = compliantCriteria * 100.0 / totalCriteria;

            conformityRate = Math.round(rate * 100.0) / 100.0;
        }

        Double qualityScore = auditRepository.calculateAverageScoreForMonthlyReport(startDate, endDate);

        if (qualityScore != null) {
            qualityScore = Math.round(qualityScore * 100.0) / 100.0;
        }

        MonthlyQualityReportDTO report = new MonthlyQualityReportDTO();

        report.setYear(year);
        report.setMonth(month);
        report.setTotalIncidents(totalIncidents);
        report.setCriticalIncidents(criticalIncidents);
        report.setTotalAudits(totalAudits);
        report.setConformityRate(conformityRate);
        report.setQualityScore(qualityScore);

        return report;
    }


    public DepartmentQhseReportDTO getDepartmentQhseReport(Long departmentId) {

        if (departmentId == null || departmentId <= 0) {
            throw new IllegalArgumentException("L'identifiant du département doit être positif");
        }

        Optional<Department> departmentResult = departmentRepository.findById(departmentId);

        if (departmentResult.isEmpty()) {
            throw new ResourceNotFoundException("Département introuvable : " + departmentId);
        }

        Department department = departmentResult.get();

        long criticalIncidents = incidentRepository.countByGravityAndDepartmentId(IncidentGravity.CRITICAL, departmentId);

        long nonCompliantCriteria = auditRepository.countNonCompliantCriteriaByDepartment(departmentId);

        List<CorrectiveActionStatus> excludedStatuses = new ArrayList<>();
        excludedStatuses.add(CorrectiveActionStatus.COMPLETED);
        excludedStatuses.add(CorrectiveActionStatus.CANCELLED);

        long overdueActions =
                correctiveActionRepository.countOverdueByDepartment(departmentId, LocalDate.now(), excludedStatuses);

        long totalCriteria = auditRepository.sumTotalCriteriaByDepartment(departmentId);

        long compliantCriteria = auditRepository.sumCompliantCriteriaByDepartment(departmentId);

        Double conformityRate = null;

        if (totalCriteria > 0) {
            double rate = compliantCriteria * 100.0 / totalCriteria;
            conformityRate = Math.round(rate * 100.0) / 100.0;
        }

        double riskScore = 0.4 * criticalIncidents + 0.3 * nonCompliantCriteria + 0.3 * overdueActions;

        riskScore = Math.round(riskScore * 100.0) / 100.0;

        DepartmentQhseReportDTO report = new DepartmentQhseReportDTO();

        report.setDepartmentId(departmentId);
        report.setDepartmentName(department.getName());
        report.setCriticalIncidents(criticalIncidents);
        report.setNonCompliantCriteria(nonCompliantCriteria);
        report.setOverdueActions(overdueActions);
        report.setTotalCriteria(totalCriteria);
        report.setCompliantCriteria(compliantCriteria);
        report.setConformityRate(conformityRate);
        report.setRiskScore(riskScore);

        return report;
    }


    public List<DepartmentIncidentStatisticsDTO> getIncidentsByDepartment() {

        List<Department> departments = departmentRepository.findAll();

        List<DepartmentIncidentStatisticsDTO> statistics = new ArrayList<>();

        for (Department department : departments) {

            long totalIncidents = incidentRepository.countByDepartmentId(department.getId());

            DepartmentIncidentStatisticsDTO dto =
                    new DepartmentIncidentStatisticsDTO();

            dto.setDepartmentId(department.getId());
            dto.setDepartmentName(department.getName());
            dto.setTotalIncidents(totalIncidents);

            statistics.add(dto);
        }

        return statistics;
    }



}