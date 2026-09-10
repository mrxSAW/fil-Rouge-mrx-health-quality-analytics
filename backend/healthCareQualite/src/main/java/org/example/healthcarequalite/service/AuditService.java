package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.audit.AuditGetDTO;
import org.example.healthcarequalite.dto.audit.AuditPostDTO;
import org.example.healthcarequalite.dto.audit.AuditUpdateDTO;
import org.example.healthcarequalite.entity.Audit;
import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.AuditMapper;
import org.example.healthcarequalite.repository.AuditRepository;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuditService {

    private final AuditRepository auditRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditMapper auditMapper;

    public AuditService( AuditRepository auditRepository, DepartmentRepository departmentRepository, AuditMapper auditMapper) {

        this.auditRepository = auditRepository;
        this.departmentRepository = departmentRepository;
        this.auditMapper = auditMapper;
    }

    public AuditGetDTO create(AuditPostDTO dto) {

        double conformityRate = calculateConformityRate(
                dto.getTotalCriteria(),
                dto.getCompliantCriteria()
        );

        Optional<Department> result =  departmentRepository.findById(dto.getDepartmentId());

        if (result.isEmpty()) {
            throw new ResourceNotFoundException( "Département introuvable : " + dto.getDepartmentId() );
        }

        Department department = result.get();

        Audit audit = auditMapper.toEntity(dto);
        audit.setDepartment(department);
        audit.setConformityRate(conformityRate);

        Audit savedAudit = auditRepository.save(audit);

        return auditMapper.toGetDTO(savedAudit);
    }

    public AuditGetDTO findById(Long id) {

        Optional<Audit> result = auditRepository.findById(id);

        if (result.isEmpty()) {
            throw new ResourceNotFoundException( "Audit introuvable : " + id );
        }

        Audit audit = result.get();

        return auditMapper.toGetDTO(audit);
    }

    public Page<AuditGetDTO> findAll( Long departmentId,  Pageable pageable) {

        Page<Audit> audits;

        if (departmentId == null) {
            audits = auditRepository.findAll(pageable);
        } else {
            audits = auditRepository.findByDepartmentId( departmentId,  pageable);
        }

        List<AuditGetDTO> auditDTOs = new ArrayList<>();

        for (Audit audit : audits.getContent()) {
            AuditGetDTO dto = auditMapper.toGetDTO(audit);
            auditDTOs.add(dto);
        }

        return new PageImpl<>( auditDTOs, pageable, audits.getTotalElements() );
    }

    public AuditGetDTO update(Long id, AuditUpdateDTO dto) {

        Optional<Audit> auditResult = auditRepository.findById(id);

        if (auditResult.isEmpty()) {
            throw new ResourceNotFoundException( "Audit introuvable : " + id );
        }

        double conformityRate = calculateConformityRate( dto.getTotalCriteria(),  dto.getCompliantCriteria() );

        Optional<Department> departmentResult =  departmentRepository.findById(dto.getDepartmentId());

        if (departmentResult.isEmpty()) {
            throw new ResourceNotFoundException( "Département introuvable : " + dto.getDepartmentId() );
        }

        Audit audit = auditResult.get();
        Department department = departmentResult.get();

        auditMapper.updateAuditFromDTO(dto, audit);
        audit.setDepartment(department);
        audit.setConformityRate(conformityRate);

        Audit updatedAudit = auditRepository.save(audit);

        return auditMapper.toGetDTO(updatedAudit);
    }

    public void delete(Long id) {

        Optional<Audit> result = auditRepository.findById(id);

        if (result.isEmpty()) {
            throw new ResourceNotFoundException( "Audit introuvable : " + id );
        }

        Audit audit = result.get();

        auditRepository.delete(audit);
    }

    private double calculateConformityRate( Integer totalCriteria, Integer compliantCriteria) {

        if (totalCriteria == null || compliantCriteria == null) {
            throw new IllegalArgumentException("Les nombres de critères sont obligatoires");
        }

        if (totalCriteria < 1) {
            throw new IllegalArgumentException("Le nombre de critères doit être au moins 1");
        }

        if (compliantCriteria < 0) {
            throw new IllegalArgumentException( "Le nombre de critères conformes ne peut pas être négatif" );
        }

        if (compliantCriteria > totalCriteria) {
            throw new IllegalArgumentException( "Le nombre de critères conformes ne peut pas dépasser le total"  );
        }

        double rate = compliantCriteria * 100.0 / totalCriteria;

        return Math.round(rate * 100.0) / 100.0;
    }



    public long countNonCompliantCriteria() {

        long total = auditRepository.countNonCompliantCriteria();

        return total;
    }



}