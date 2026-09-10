package org.example.healthcarequalite.repository;

import org.example.healthcarequalite.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}