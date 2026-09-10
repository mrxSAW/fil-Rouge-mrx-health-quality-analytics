package org.example.healthcarequalite.repository;

import org.example.healthcarequalite.dto.statistics.MonthlyIncidentDTO;
import org.example.healthcarequalite.entity.Incident;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentStatus;
import org.example.healthcarequalite.enums.IncidentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    @Query("""
        SELECT i FROM Incident i
        WHERE (:type IS NULL OR i.type = :type)
          AND (:gravity IS NULL OR i.gravity = :gravity)  AND (:status IS NULL OR i.status = :status)
          AND (:departmentId IS NULL OR i.department.id = :departmentId) AND (:reporterEmail IS NULL OR i.reporter.email = :reporterEmail)
        """)
    Page<Incident> search(   @Param("type") IncidentType type, @Param("gravity") IncidentGravity gravity,
                             @Param("status") IncidentStatus status, @Param("departmentId") Long departmentId,
                             @Param("reporterEmail") String reporterEmail, Pageable pageable
                               );

    long countByGravity(IncidentGravity gravity);


    @Query("""
    SELECT new org.example.healthcarequalite.dto.statistics.MonthlyIncidentDTO(
        YEAR(i.incidentDate),MONTH(i.incidentDate),COUNT(i)  )
    FROM Incident i
    WHERE i.incidentDate IS NOT NULL
    GROUP BY YEAR(i.incidentDate), MONTH(i.incidentDate)
    ORDER BY YEAR(i.incidentDate), MONTH(i.incidentDate)
    """)
    List<MonthlyIncidentDTO> countIncidentsByMonth();


    long countByType(IncidentType type);


    long countByGravityAndDepartmentId( IncidentGravity gravity, Long departmentId );


    @Query("""
    SELECT COUNT(i)
    FROM Incident i
    WHERE i.department.id = :departmentId  AND i.incidentDate >= :startDate  AND i.incidentDate < :endDate
    """)
    long countIncidentsForPeriod( @Param("departmentId") Long departmentId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Incident> findByDepartmentId( Long departmentId, Sort sort);


    @Query("""
    SELECT i FROM Incident i
    WHERE (:departmentId IS NULL OR i.department.id = :departmentId)
      AND (:startDate IS NULL OR i.incidentDate >= :startDate) AND (:endDate IS NULL OR i.incidentDate <= :endDate)
    """)
    List<Incident> findForReport( @Param("departmentId") Long departmentId, @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate, Sort sort  );






    @Query("""
    SELECT COUNT(i) FROM Incident i
    WHERE i.incidentDate >= :startDate AND i.incidentDate < :endDate
    """)
    long countForMonthlyReport( @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate );

    @Query("""
    SELECT COUNT(i) FROM Incident i
    WHERE i.incidentDate >= :startDate  AND i.incidentDate < :endDate  AND i.gravity = :gravity
    """)
    long countByGravityForMonthlyReport( @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("gravity") IncidentGravity gravity);


    long countByDepartmentId(Long departmentId);



}