package org.example.healthcarequalite.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.healthcarequalite.enums.Role;

@Entity
@Table(name="users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false)
  private String firstName;

  @Column(nullable=false)
  private String lastName;

  @Column(nullable = false)
  private String username ;

  @Email @Column(nullable=false, unique=true)
  private String email;

  @JsonIgnore @Column(nullable=false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false)
  private Role role;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="department_id")
  private Department department;

}
