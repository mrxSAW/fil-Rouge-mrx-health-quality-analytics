package org.example.healthcarequalite.dto.department;

import lombok.Data;

import java.io.Serializable;

@Data
public class DepartmentGetDTO implements Serializable {

    private Long id;
    private String name;
    private String description;
}