package org.example.healthcarequalite.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name="departments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Department {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable=false, unique=true)
  private String name;

  private String description;
}
