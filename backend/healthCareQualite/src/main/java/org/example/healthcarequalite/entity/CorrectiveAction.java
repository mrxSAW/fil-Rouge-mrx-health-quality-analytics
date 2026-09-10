package org.example.healthcarequalite.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.healthcarequalite.enums.CorrectiveActionStatus;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="Corrective_Action")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorrectiveAction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank(message = "le titre et obligatoire ")
  private String title;

  @NotBlank(message = "la description est obligatoire")
  private String  description;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private CorrectiveActionStatus status;

  @Column(nullable=false)
  @NotNull(message = "le deadline est obligatoire ")
  private LocalDate deadline;

  @ManyToOne(optional = false)
  @JoinColumn(name = "incident_id")
  private Incident   incident;

  @ManyToOne(optional = false)
  @JoinColumn(name = "responsible_user_id")
  private User  responsibleUser;


}
