package org.example.healthcarequalite.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Audits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "La date d'audit est obligatoire")
    private LocalDate auditDate;

    @NotNull(message = "Le score est obligatoire")
    @Min(value = 0, message = "Le score doit être supérieur ou égal à 0")
    @Max(value = 100, message = "Le score doit être inférieur ou égal à 100")
    private Integer score;

    @NotNull(message = "Le nombre de critères est obligatoire")
    @Min(value = 1, message = "Le nombre de critères doit être au moins 1")
    private Integer totalCriteria;

    @NotNull(message = "Le nombre de critères conformes est obligatoire")
    @Min(value = 0, message = "Le nombre de critères conformes ne peut pas être négatif")
    private Integer compliantCriteria;

    @NotNull(message = "Le taux de conformité est obligatoire")
    private Double conformityRate;

    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}