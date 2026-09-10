package org.example.healthcarequalite.repository;

import org.example.healthcarequalite.entity.MonthlyAdmissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyAdmissionsRepository extends JpaRepository<MonthlyAdmissions, Long> {

    Optional<MonthlyAdmissions> findByDepartmentIdAndYearAndMonth( Long departmentId, Integer year, Integer month );

    boolean existsByDepartmentIdAndYearAndMonth( Long departmentId, Integer year, Integer month );


    Page<MonthlyAdmissions> findByDepartmentId(Long departmentId, Pageable pageable );


}