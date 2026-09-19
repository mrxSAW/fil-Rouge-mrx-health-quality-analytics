package org.example.healthcarequalite.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.healthcarequalite.enums.ReportType;

import java.time.LocalDate;

@Entity
@Table(name = "report")
@Getter  @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "le titre est obligatoire")
    private String   title;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ReportType type;

    @NotNull(message = "la date est obligatoire")
    private LocalDate createdAt;


  @NotBlank(message = "url est obligatoire")
  private String  fileUrl;

  @ManyToOne
  @JoinColumn(name="department_id")
    private Department department;

    @Lob
    @Column(name = "file_content", columnDefinition = "LONGBLOB")
    private byte[] fileContent;

}
