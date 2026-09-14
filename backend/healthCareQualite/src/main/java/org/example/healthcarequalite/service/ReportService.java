package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.report.ReportGetDTO;
import org.example.healthcarequalite.entity.Report;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.ReportMapper;
import org.example.healthcarequalite.repository.ReportRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public ReportService(ReportRepository reportRepository, ReportMapper reportMapper) {

        this.reportRepository = reportRepository;
        this.reportMapper = reportMapper;
    }

    public Page<ReportGetDTO> findAll(Pageable pageable) {

        Page<Report> reports = reportRepository.findAll(pageable);

        return reports.map(reportMapper::toGetDTO);
    }

    public ReportGetDTO findById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'identifiant du rapport doit être positif");
        }

        Optional<Report> reportResult = reportRepository.findById(id);

        if (reportResult.isEmpty()) {
            throw new ResourceNotFoundException("Rapport introuvable : " + id);
        }

        Report report = reportResult.get();

        return reportMapper.toGetDTO(report);
    }
}