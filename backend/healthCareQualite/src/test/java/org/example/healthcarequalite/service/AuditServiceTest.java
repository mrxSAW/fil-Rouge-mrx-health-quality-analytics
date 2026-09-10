package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.audit.AuditGetDTO;
import org.example.healthcarequalite.dto.audit.AuditPostDTO;
import org.example.healthcarequalite.dto.audit.AuditUpdateDTO;
import org.example.healthcarequalite.entity.Audit;
import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.mapper.AuditMapper;
import org.example.healthcarequalite.repository.AuditRepository;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuditServiceTest {

    @Test
    void createShouldRejectMoreCompliantCriteriaThanTotal() {

        AuditRepository auditRepository = mock(AuditRepository.class);
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        AuditMapper auditMapper = mock(AuditMapper.class);

        AuditService auditService = new AuditService( auditRepository, departmentRepository, auditMapper);

        AuditPostDTO dto = new AuditPostDTO();
        dto.setTotalCriteria(14);
        dto.setCompliantCriteria(15);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> auditService.create(dto) );

        assertEquals("Le nombre de critères conformes ne peut pas dépasser le total", exception.getMessage() );

        verifyNoInteractions( auditRepository, departmentRepository, auditMapper);
    }


    @Test
    void createShouldCalculateConformityRate() {

        AuditRepository auditRepository = mock(AuditRepository.class);
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        AuditMapper auditMapper = mock(AuditMapper.class);

        AuditService auditService = new AuditService( auditRepository, departmentRepository, auditMapper );

        AuditPostDTO dto = new AuditPostDTO();
        dto.setTitle("Audit de test");
        dto.setAuditDate(LocalDate.of(2026, 9, 8));
        dto.setScore(80);
        dto.setTotalCriteria(14);
        dto.setCompliantCriteria(5);
        dto.setDepartmentId(1L);

        Department department = new Department();
        department.setId(1L);
        department.setName("CARDIOLOGIE");

        Audit audit = new Audit();
        audit.setTotalCriteria(14);
        audit.setCompliantCriteria(5);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        when(auditMapper.toEntity(dto)).thenReturn(audit);

        when(auditRepository.save(any(Audit.class))).thenReturn(audit);

        when(auditMapper.toGetDTO(audit)).thenReturn(new AuditGetDTO());

        auditService.create(dto);

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);

        verify(auditRepository).save(captor.capture());

        Audit savedAudit = captor.getValue();

        assertEquals(35.71, savedAudit.getConformityRate(), 0.001);
        assertEquals(1L, savedAudit.getDepartment().getId());
    }


    @Test
    void createShouldRejectZeroTotalCriteria() {

        AuditRepository auditRepository = mock(AuditRepository.class);
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        AuditMapper auditMapper = mock(AuditMapper.class);

        AuditService auditService = new AuditService( auditRepository, departmentRepository, auditMapper );

        AuditPostDTO dto = new AuditPostDTO();
        dto.setTotalCriteria(0);
        dto.setCompliantCriteria(0);

        IllegalArgumentException exception = assertThrows( IllegalArgumentException.class, () -> auditService.create(dto));

        assertEquals("Le nombre de critères doit être au moins 1", exception.getMessage() );

        verifyNoInteractions( auditRepository, departmentRepository, auditMapper );
    }



    @Test
    void updateShouldRecalculateConformityRate() {

        AuditRepository auditRepository = mock(AuditRepository.class);
        DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
        AuditMapper auditMapper = mock(AuditMapper.class);

        AuditService auditService = new AuditService( auditRepository,  departmentRepository, auditMapper);

        Department department = new Department();
        department.setId(1L);

        Audit existingAudit = new Audit();
        existingAudit.setId(1L);
        existingAudit.setTotalCriteria(14);
        existingAudit.setCompliantCriteria(5);
        existingAudit.setConformityRate(35.71);
        existingAudit.setDepartment(department);

        AuditUpdateDTO dto = new AuditUpdateDTO();
        dto.setTitle("Audit modifié");
        dto.setAuditDate(LocalDate.of(2026, 9, 8));
        dto.setScore(90);
        dto.setTotalCriteria(14);
        dto.setCompliantCriteria(7);
        dto.setDepartmentId(1L);

        when(auditRepository.findById(1L)).thenReturn(Optional.of(existingAudit));

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        when(auditRepository.save(any(Audit.class))).thenReturn(existingAudit);

        when(auditMapper.toGetDTO(existingAudit)).thenReturn(new AuditGetDTO());

        auditService.update(1L, dto);

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);

        verify(auditRepository).save(captor.capture());

        Audit savedAudit = captor.getValue();

        assertEquals(50.0, savedAudit.getConformityRate(), 0.001);

        verify(auditMapper).updateAuditFromDTO(dto, existingAudit);
    }




}