package org.example.healthcarequalite.repository;

import org.example.healthcarequalite.entity.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AuditRepository extends JpaRepository<Audit, Long> {

    Page<Audit> findByDepartmentId(Long departmentId, Pageable pageable);


    @Query("""
    SELECT COALESCE(SUM(a.totalCriteria - a.compliantCriteria), 0)
    FROM Audit a
    WHERE a.totalCriteria IS NOT NULL
      AND a.compliantCriteria IS NOT NULL
    """)
    long countNonCompliantCriteria();


    @Query("""
    SELECT COALESCE(SUM(a.totalCriteria), 0)
    FROM Audit a
    WHERE a.totalCriteria IS NOT NULL
      AND a.compliantCriteria IS NOT NULL
    """)
    long sumTotalCriteria();

    @Query("""
    SELECT COALESCE(SUM(a.compliantCriteria), 0)
    FROM Audit a
    WHERE a.totalCriteria IS NOT NULL
      AND a.compliantCriteria IS NOT NULL
    """)
    long sumCompliantCriteria();



    @Query("""
    SELECT COALESCE(SUM(a.totalCriteria), 0)
    FROM Audit a
    WHERE a.department.id = :departmentId AND a.totalCriteria IS NOT NULL AND a.compliantCriteria IS NOT NULL
    """)
    long sumTotalCriteriaByDepartment( @Param("departmentId") Long departmentId );

    @Query("""
    SELECT COALESCE(SUM(a.compliantCriteria), 0)
    FROM Audit a
    WHERE a.department.id = :departmentId AND a.totalCriteria IS NOT NULL AND a.compliantCriteria IS NOT NULL
    """)
    long sumCompliantCriteriaByDepartment( @Param("departmentId") Long departmentId  );


    @Query("""
    SELECT COALESCE(SUM(a.totalCriteria - a.compliantCriteria), 0)
    FROM Audit a
    WHERE a.department.id = :departmentId  AND a.totalCriteria IS NOT NULL  AND a.compliantCriteria IS NOT NULL
    """)
    long countNonCompliantCriteriaByDepartment( @Param("departmentId") Long departmentId );



    @Query("""
            SELECT AVG(a.score) FROM Audit a
           """)
    Double calculateAverageScore();




    @Query("""
    SELECT a FROM Audit a
    WHERE (:departmentId IS NULL OR a.department.id = :departmentId)AND (:startDate IS NULL OR a.auditDate >= :startDate)
      AND (:endDate IS NULL OR a.auditDate <= :endDate)
    """)
    List<Audit> findForReport(
            @Param("departmentId") Long departmentId, @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate, Sort sort
    );





    @Query("""
    SELECT COUNT(a) FROM Audit a
    WHERE a.auditDate >= :startDate  AND a.auditDate < :endDate
    """)
    long countForMonthlyReport( @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate );

    @Query("""
    SELECT COALESCE(SUM(a.totalCriteria), 0)  FROM Audit a
    WHERE a.auditDate >= :startDate AND a.auditDate < :endDate AND a.totalCriteria IS NOT NULL AND a.compliantCriteria IS NOT NULL
    """)
    long sumTotalCriteriaForMonthlyReport( @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate );

    @Query("""
    SELECT COALESCE(SUM(a.compliantCriteria), 0)  FROM Audit a
    WHERE a.auditDate >= :startDate  AND a.auditDate < :endDate AND a.totalCriteria IS NOT NULL AND a.compliantCriteria IS NOT NULL
    """)
    long sumCompliantCriteriaForMonthlyReport( @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate );

    @Query("""
    SELECT AVG(a.score) FROM Audit a
    WHERE a.auditDate >= :startDate  AND a.auditDate < :endDate
    """)
    Double calculateAverageScoreForMonthlyReport( @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate );






}