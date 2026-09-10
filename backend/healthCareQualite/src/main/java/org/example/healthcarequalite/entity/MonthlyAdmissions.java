package org.example.healthcarequalite.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "monthly_admissions", uniqueConstraints = @UniqueConstraint( columnNames = {"department_id", "admission_year", "admission_month"}))
@Getter
@Setter
@NoArgsConstructor
public class MonthlyAdmissions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "L'année est obligatoire")
    @Min(value = 1, message = "L'année doit être positive")
    @Column(name = "admission_year", nullable = false)
    private Integer year;

    @NotNull(message = "Le mois est obligatoire")
    @Min(value = 1, message = "Le mois doit être compris entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être compris entre 1 et 12")
    @Column(name = "admission_month", nullable = false)
    private Integer month;

    @NotNull(message = "Le nombre d'admissions est obligatoire")
    @Min(value = 0, message = "Le nombre d'admissions ne peut pas être négatif")
    @Column(nullable = false)
    private Integer admissionCount;

    @NotNull(message = "Le département est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}