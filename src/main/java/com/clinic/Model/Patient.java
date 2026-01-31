package com.clinic.Model;

import com.clinic.Enums.ClinicStatus;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clinic_patient",
        indexes = {
                @Index(name = "idx_patient_contact", columnList = "contact_number"),
                @Index(name = "idx_patient_email", columnList = "patient_email")
        })
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "patient_id")
    @Hidden
    private UUID patientId;

    // ===== Clinic Mapping =====
    @Hidden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    // ===== Doctor Mapping (optional) =====
    @Hidden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // ===== Personal Info =====
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "blood_group")
    private String bloodGroup;

    // ===== Contact Info =====
    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "patient_email")
    private String patientEmail;

    @Column(name = "patient_password")
    private String patientPassword;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pin_code")
    private String pinCode;

    // ===== Medical Info =====
    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "allergies")
    private String allergies;

    @Column(name = "existing_diseases")
    private String existingDiseases;

    @Column(name = "current_medications")
    private String currentMedications;

    // ===== Status =====
    @Enumerated(EnumType.STRING)
    @Column(name = "patient_status")
    private ClinicStatus patientStatus;

    // ===== Role =====
    @Hidden
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "patient_roles",
            joinColumns = @JoinColumn(name = "patient_id"),
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<Roles> roles;

    // ===== Audit =====

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Hidden
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.patientStatus = ClinicStatus.ACTIVE;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

