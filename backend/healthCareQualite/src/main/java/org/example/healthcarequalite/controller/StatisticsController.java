package org.example.healthcarequalite.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.healthcarequalite.dto.statistics.*;
import org.example.healthcarequalite.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'QUALITY_MANAGER', 'QHSE_MANAGER')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {

        this.statisticsService = statisticsService;
    }

    @GetMapping("/risk")
    public ResponseEntity<RiskStatisticsDTO> getRiskStatistics() {

        RiskStatisticsDTO statistics = statisticsService.getRiskStatistics();

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/conformity")
    public ResponseEntity<ConformityStatisticsDTO> getConformityStatistics() {

        ConformityStatisticsDTO statistics = statisticsService.getConformityStatistics();

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/incidents/monthly")
    public ResponseEntity<List<MonthlyIncidentDTO>> getMonthlyIncidents() {

        List<MonthlyIncidentDTO> statistics = statisticsService.getMonthlyIncidents();

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/incidents/by-gravity")
    public ResponseEntity<List<IncidentGravityStatisticsDTO>> getIncidentsByGravity() {

        List<IncidentGravityStatisticsDTO> statistics = statisticsService.getIncidentsByGravity();

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/incidents/by-type")
    public ResponseEntity<List<IncidentTypeStatisticsDTO>> getIncidentsByType() {

        List<IncidentTypeStatisticsDTO> statistics = statisticsService.getIncidentsByType();

        return ResponseEntity.ok(statistics);
    }



    @GetMapping("/conformity/by-department")
    public ResponseEntity<List<DepartmentConformityDTO>>  getConformityByDepartment() {

        List<DepartmentConformityDTO> statistics = statisticsService.getConformityByDepartment();

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/risk/by-department")
    public ResponseEntity<List<DepartmentRiskDTO>> getRiskByDepartment() {

        List<DepartmentRiskDTO> statistics = statisticsService.getRiskByDepartment();

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/incidents/rate")
    public ResponseEntity<IncidentRateDTO> getIncidentRate(@RequestParam Long departmentId, @RequestParam Integer year, @RequestParam Integer month) {

        IncidentRateDTO statistics = statisticsService.getIncidentRate( departmentId, year, month );

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/quality-score")
    public ResponseEntity<QualityScoreDTO> getQualityScore() {

        QualityScoreDTO statistics = statisticsService.getQualityScore();

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {

        DashboardDTO dashboard = statisticsService.getDashboard();

        return ResponseEntity.ok(dashboard);
    }



    @GetMapping("/monthly-quality")
    public ResponseEntity<MonthlyQualityReportDTO> getMonthlyQualityReport( @RequestParam Integer year, @RequestParam Integer month) {

        MonthlyQualityReportDTO report = statisticsService.getMonthlyQualityReport(year, month);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/departments/{departmentId}/qhse")
    public ResponseEntity<DepartmentQhseReportDTO> getDepartmentQhseReport(@PathVariable Long departmentId) {

        DepartmentQhseReportDTO report = statisticsService.getDepartmentQhseReport(departmentId);

        return ResponseEntity.ok(report);
    }



    @GetMapping("/incidents/by-department")
    public ResponseEntity<List<DepartmentIncidentStatisticsDTO>> getIncidentsByDepartment() {

        List<DepartmentIncidentStatisticsDTO> statistics = statisticsService.getIncidentsByDepartment();

        return ResponseEntity.ok(statistics);
    }


}