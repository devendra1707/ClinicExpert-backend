package com.clinic.Model;

import com.clinic.Enums.ClinicStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "Clinic_Doctor_Table",
        indexes = {
                @Index(name = "idx_doctor_id",columnList = "doctor_id"),
                @Index(name = "idx_doctor_email",columnList = "doctor_email"),
                @Index(name = "idx_doctor_contact",columnList = "doctor_contact"),

               })
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "doctor_id",updatable = false,unique = true)
    @Hidden
    private UUID doctorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    @JsonBackReference
    @Hidden
    private Clinic clinic;

    @Column(name = "doctor_full_name")
    private String fullName;

    @Column(name = "doctor_specialization")
    private  String doctorSpecialization;

    @Column(name = "doctor_contact")
    private  String doctorContact;

    @Column(name = "doctor_email")
    private String doctorEmail;

    @Column(name = "doctor_qualification")
    private String doctorQualification;

    @Column(name = "doctor_experience")
    private String doctorExperience;

    @Column(name = "doctor_available_from")
    private String doctorAvailableFrom;

    @Column(name = "doctor_available_to")
    private String DoctorAvailableTo;

    @Column(name = "doctor_status")
    private ClinicStatus status;

    @Column(name = "create_at", updatable = false)
    @Hidden
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    @Hidden
    private LocalDateTime updateAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
        System.out.println("===Before Insert Data====");
    }

    @PostPersist
    public void postPersist() {
        this.updateAt = LocalDateTime.now();
        System.out.println("===After Insert Data===");
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "doctor_roles",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<Roles> roles;
}
