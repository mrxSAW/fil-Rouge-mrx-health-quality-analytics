package org.example.healthcarequalite.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Quality_Indicator")
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class QualityIndicator {


   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @NotBlank(message = "titre obligatoire")
   private String name;

   @NotNull(message = "La valeur est obligatoire")
   private Integer value;

   @NotNull(message = "La valeur cible est obligatoire")
   private Integer targetValue;

   @NotBlank(message = "la preriod et obligatoire")
   private String period;

   @ManyToOne
   @JoinColumn(name="department_id")
   private Department department;



}
