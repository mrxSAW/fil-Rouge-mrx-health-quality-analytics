package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.report.ReportGetDTO;
import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.entity.Report;
import org.example.healthcarequalite.enums.ReportType;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.ReportMapper;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.example.healthcarequalite.repository.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final DepartmentRepository departmentRepository;

    public ReportService( ReportRepository reportRepository, ReportMapper reportMapper, DepartmentRepository departmentRepository) {

        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
        this.departmentRepository = departmentRepository;
    }

    public Page<ReportGetDTO> findAll( Long departmentId, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        if (departmentId != null && departmentId <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du département doit être positif"
            );
        }

        if (startDate != null &&
                endDate != null &&
                startDate.isAfter(endDate)) {

            throw new IllegalArgumentException(
                    "La date de début doit précéder ou être égale à la date de fin"
            );
        }

        Page<Report> reports = reportRepository.search(
                departmentId,
                startDate,
                endDate,
                pageable
        );

        return reports.map(report -> reportMapper.toGetDTO(report));
    }

    public ReportGetDTO findById(Long id) {

        Report report = findEntityById(id);

        return reportMapper.toGetDTO(report);
    }

    @Transactional
    public Report saveGeneratedReport( String title, ReportType type, byte[] fileContent, Long departmentId) {

        if (fileContent == null || fileContent.length == 0) {
            throw new IllegalArgumentException( "Le fichier du rapport est vide");
        }

        Department department = null;

        if (departmentId != null) {
            department = departmentRepository.findById(departmentId).orElseThrow(() ->
                 new ResourceNotFoundException( "Département introuvable : " + departmentId) );
        }

        Report report = new Report();

        report.setTitle(title);
        report.setType(type);
        report.setCreatedAt(LocalDate.now());
        report.setDepartment(department);
        report.setFileContent(fileContent);

        // L'identifiant sera disponible après l'enregistrement.
        report.setFileUrl("/api/reports");

        Report savedReport = reportRepository.save(report);

        savedReport.setFileUrl(
                "/api/reports/" + savedReport.getId() + "/download"
        );

        return reportRepository.save(savedReport);
    }

    public Report getStoredReport(Long id) {

        Report report = findEntityById(id);

        if (report.getFileContent() == null ||
                report.getFileContent().length == 0) {

            throw new ResourceNotFoundException(
                    "Aucun fichier conservé pour ce rapport"
            );
        }

        return report;
    }

    private Report findEntityById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du rapport doit être positif"
            );
        }

        return reportRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Rapport introuvable : " + id
                        )
                );
    }




}