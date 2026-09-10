package org.example.healthcarequalite.repository;

import org.example.healthcarequalite.entity.QualityIndicator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityIndicatorRepository extends JpaRepository<QualityIndicator, Long> {
}