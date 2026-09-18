package org.example.healthcarequalite.repository;

import org.example.healthcarequalite.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("""
        SELECT r FROM Report r
        WHERE (:departmentId IS NULL OR r.department.id = :departmentId)
          AND (:startDate IS NULL OR r.createdAt >= :startDate)
          AND (:endDate IS NULL OR r.createdAt <= :endDate)
        """)
    Page<Report> search(@Param("departmentId") Long departmentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                           Pageable pageable );
}