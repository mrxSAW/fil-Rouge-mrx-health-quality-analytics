package org.example.healthcarequalite.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.healthcarequalite.enums.IncidentGravity;
import org.example.healthcarequalite.enums.IncidentStatus;
import org.example.healthcarequalite.enums.IncidentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="incidents")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Incident {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY) private
  Long id;

  @NotBlank
  @Column(nullable=false)
  private String title;

  @Column(nullable=false, length=3000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private IncidentStatus status;

  @Column(nullable=false, updatable=false)
  private LocalDateTime createdAt;

  @ManyToOne(optional=false)
  @JoinColumn(name="department_id")
  private Department department;

  @ManyToOne(optional=false)
  @JoinColumn(name="reporter_id")
  private User reporter;

  @PrePersist void prePersist() {
    if (createdAt == null ) {
      createdAt = LocalDateTime.now();
    }

    if (status == null){
      status=IncidentStatus.OPEN;
    }

  }


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncidentType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncidentGravity gravity;

  @Column(nullable = false)
  private LocalDate incidentDate;



}
