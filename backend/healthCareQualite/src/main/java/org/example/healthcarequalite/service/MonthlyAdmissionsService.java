package org.example.healthcarequalite.service;

import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsGetDTO;
import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsPostDTO;
import org.example.healthcarequalite.dto.admissions.MonthlyAdmissionsUpdateDTO;
import org.example.healthcarequalite.entity.Department;
import org.example.healthcarequalite.entity.MonthlyAdmissions;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.mapper.MonthlyAdmissionsMapper;
import org.example.healthcarequalite.repository.DepartmentRepository;
import org.example.healthcarequalite.repository.MonthlyAdmissionsRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MonthlyAdmissionsService {

    private final MonthlyAdmissionsRepository monthlyAdmissionsRepository;
    private final DepartmentRepository departmentRepository;
    private final MonthlyAdmissionsMapper monthlyAdmissionsMapper;

    public MonthlyAdmissionsService( MonthlyAdmissionsRepository monthlyAdmissionsRepository, DepartmentRepository departmentRepository, MonthlyAdmissionsMapper monthlyAdmissionsMapper) {

        this.monthlyAdmissionsRepository = monthlyAdmissionsRepository;
        this.departmentRepository = departmentRepository;
        this.monthlyAdmissionsMapper = monthlyAdmissionsMapper;
    }

    @CacheEvict(cacheNames = "statistics", allEntries = true)
    public MonthlyAdmissionsGetDTO create(MonthlyAdmissionsPostDTO dto) {

        Optional<Department> result = departmentRepository.findById(dto.getDepartmentId());

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Département introuvable : " + dto.getDepartmentId() );
        }

        boolean alreadyExists =
                monthlyAdmissionsRepository.existsByDepartmentIdAndYearAndMonth(
                        dto.getDepartmentId(),
                        dto.getYear(),
                        dto.getMonth()
                );

        if (alreadyExists) {
            throw new IllegalArgumentException( "Les admissions de ce département sont déjà enregistrées " + "pour ce mois. Modifiez la saisie existante.");
        }

        Department department = result.get();

        MonthlyAdmissions admissions = monthlyAdmissionsMapper.toEntity(dto);

        admissions.setDepartment(department);

        MonthlyAdmissions savedAdmissions =  monthlyAdmissionsRepository.save(admissions);

        return monthlyAdmissionsMapper.toGetDTO(savedAdmissions);
    }

    public Page<MonthlyAdmissionsGetDTO> findAll( Long departmentId, Pageable pageable) {

        Page<MonthlyAdmissions> admissions;

        if (departmentId == null) {
            admissions = monthlyAdmissionsRepository.findAll(pageable);
        } else {
            admissions = monthlyAdmissionsRepository.findByDepartmentId( departmentId, pageable );
        }

        List<MonthlyAdmissionsGetDTO> admissionDTOs = new ArrayList<>();

        for (MonthlyAdmissions admission : admissions.getContent()) {

            MonthlyAdmissionsGetDTO dto = monthlyAdmissionsMapper.toGetDTO(admission);

            admissionDTOs.add(dto);
        }

        return new PageImpl<>(admissionDTOs, pageable, admissions.getTotalElements());
    }

    public MonthlyAdmissionsGetDTO findById(Long id) {

        Optional<MonthlyAdmissions> result = monthlyAdmissionsRepository.findById(id);

        if (result.isEmpty()) {
            throw new ResourceNotFoundException( "Saisie d'admissions introuvable : " + id );
        }

        MonthlyAdmissions admissions = result.get();

        return monthlyAdmissionsMapper.toGetDTO(admissions);
    }


    @CacheEvict(cacheNames = "statistics", allEntries = true)
    public MonthlyAdmissionsGetDTO update( Long id, MonthlyAdmissionsUpdateDTO dto) {

        Optional<MonthlyAdmissions> result = monthlyAdmissionsRepository.findById(id);

        if (result.isEmpty()) {
            throw new ResourceNotFoundException( "Saisie d'admissions introuvable : " + id );
        }

        MonthlyAdmissions admissions = result.get();

        monthlyAdmissionsMapper.updateFromDTO(dto, admissions);

        MonthlyAdmissions updatedAdmissions = monthlyAdmissionsRepository.save(admissions);

        return monthlyAdmissionsMapper.toGetDTO(updatedAdmissions);
    }





}