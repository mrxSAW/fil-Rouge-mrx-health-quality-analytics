package org.example.healthcarequalite.repository;

import org.example.healthcarequalite.entity.CorrectiveAction;
import org.example.healthcarequalite.enums.CorrectiveActionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CorrectiveActionRepository extends JpaRepository<CorrectiveAction, Long> {

    Page<CorrectiveAction> findByResponsibleUserId( Long responsibleUserId, Pageable pageable );



    @Query("""
    SELECT a FROM CorrectiveAction a
    WHERE a.deadline < :today
      AND a.status NOT IN :excludedStatuses AND (:responsibleUserId IS NULL OR a.responsibleUser.id = :responsibleUserId)
    """)
    Page<CorrectiveAction> findOverdue( @Param("today") LocalDate today, @Param("excludedStatuses")
            List<CorrectiveActionStatus> excludedStatuses, @Param("responsibleUserId") Long responsibleUserId,
            Pageable pageable
    );




    @Query("""
    SELECT COUNT(a) FROM CorrectiveAction a
    WHERE a.deadline < :today
      AND a.status NOT IN :excludedStatuses
    """)
    long countOverdue( @Param("today") LocalDate today, @Param("excludedStatuses")
            List<CorrectiveActionStatus> excludedStatuses
    );




    @Query("""
    SELECT COUNT(a)
    FROM CorrectiveAction a
    WHERE a.incident.department.id = :departmentId  AND a.deadline < :today  AND a.status NOT IN :excludedStatuses
    """)
    long countOverdueByDepartment( @Param("departmentId") Long departmentId, @Param("today") LocalDate today, @Param("excludedStatuses")
            List<CorrectiveActionStatus> excludedStatuses
    );




}