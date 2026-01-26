package com.clinic.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "clinic_role",
        indexes = {
                @Index(name = "idx_role_name", columnList = "role_name")
        }
)
@Data
public class Roles {

    @Id
    @NotBlank
    @Column(name = "role_name")
    private String roleName;

    @NotBlank
    @Column(name = "role_description")
    private String roleDescription;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<Clinic> clinics;


    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<Doctor> doctors;
}
