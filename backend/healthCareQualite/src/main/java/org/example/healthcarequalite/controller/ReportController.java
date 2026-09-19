package org.example.healthcarequalite.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.healthcarequalite.dto.report.ReportGetDTO;
import org.example.healthcarequalite.entity.Report;
import org.example.healthcarequalite.enums.ReportType;
import org.example.healthcarequalite.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'QUALITY_MANAGER', 'QHSE_MANAGER')")
public class ReportController {

    private final IncidentPdfService incidentPdfService;
    private final AuditPdfService auditPdfService;
    private final IncidentExcelService incidentExcelService;
    private final MonthlyQualityPdfService monthlyQualityPdfService;
    private final DepartmentQhsePdfService departmentQhsePdfService;
    private final ReportService reportService;

    public ReportController(IncidentPdfService incidentPdfService, AuditPdfService auditPdfService, IncidentExcelService incidentExcelService, MonthlyQualityPdfService monthlyQualityPdfService, DepartmentQhsePdfService departmentQhsePdfService, ReportService reportService) {

        this.incidentPdfService = incidentPdfService;
        this.auditPdfService = auditPdfService;
        this.incidentExcelService = incidentExcelService;
        this.monthlyQualityPdfService = monthlyQualityPdfService;
        this.departmentQhsePdfService = departmentQhsePdfService;
        this.reportService = reportService;
    }

    @GetMapping(value = "/incidents/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadIncidentPdf(@RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
            throws IOException {

        byte[] pdf = incidentPdfService.generateReport(
                departmentId,
                startDate,
                endDate
        );

        reportService.saveGeneratedReport(
                "Rapport des incidents",
                ReportType.INCIDENT_PDF,
                pdf,
                departmentId
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("rapport-incidents.pdf")
                                .build()
                                .toString()
                )
                .body(pdf);
    }

    @GetMapping(value = "/audits/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadAuditPdf(@RequestParam(required = false) Long departmentId, @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws IOException {

        byte[] pdf = auditPdfService.generateReport(
                departmentId,
                startDate,
                endDate
        );

        reportService.saveGeneratedReport(
                "Rapport des audits",
                ReportType.AUDIT_PDF,
                pdf,
                departmentId
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("rapport-audits.pdf")
                                .build()
                                .toString()
                )
                .body(pdf);
    }

    @GetMapping(value = "/incidents/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> downloadIncidentExcel(@RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
            throws IOException {

        byte[] excel = incidentExcelService.generateReport(
                departmentId,
                startDate,
                endDate
        );

        reportService.saveGeneratedReport(
                "Export Excel des incidents",
                ReportType.INCIDENT_EXCEL,
                excel,
                departmentId
        );

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("rapport-incidents.xlsx")
                                .build()
                                .toString()
                )
                .body(excel);
    }

    @GetMapping(value = "/monthly-quality/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadMonthlyQualityPdf(@RequestParam Integer year,
            @RequestParam Integer month) throws IOException {

        byte[] pdf = monthlyQualityPdfService.generateReport(year, month);

        reportService.saveGeneratedReport(
                "Rapport qualité " + month + "/" + year,
                ReportType.MONTHLY_QUALITY,
                pdf,
                null
        );

        String filename = "rapport-qualite-" + year + "-" + month + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename)
                                .build()
                                .toString()
                )
                .body(pdf);
    }

    @GetMapping(value = "/departments/{departmentId}/qhse/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadDepartmentQhsePdf(@PathVariable Long departmentId) throws IOException {

        byte[] pdf = departmentQhsePdfService.generateReport(departmentId);

        reportService.saveGeneratedReport(
                "Rapport QHSE par département",
                ReportType.QHSE_DEPARTMENT,
                pdf,
                departmentId
        );

        String filename =
                "rapport-qhse-departement-" + departmentId + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename)
                                .build()
                                .toString()
                )
                .body(pdf);
    }

    @GetMapping
    public ResponseEntity<Page<ReportGetDTO>> findAll(@RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Le numéro de page doit être positif ou égal à zéro"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "La taille de la page doit être comprise entre 1 et 100"
            );
        }

        Sort sort = Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ReportGetDTO> reports = reportService.findAll(
                departmentId,
                startDate,
                endDate,
                pageable
        );

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportGetDTO> findById(@PathVariable Long id) {

        ReportGetDTO report = reportService.findById(id);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadStoredReport(@PathVariable Long id) {

        Report report = reportService.getStoredReport(id);

        String contentType;
        String extension;

        if (report.getType() == ReportType.INCIDENT_EXCEL) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            extension = ".xlsx";
        } else {
            contentType = MediaType.APPLICATION_PDF_VALUE;
            extension = ".pdf";
        }

        String filename = "rapport-" + report.getId() + extension;

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename)
                                .build()
                                .toString()
                )
                .body(report.getFileContent());
    }
}