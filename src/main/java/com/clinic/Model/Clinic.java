package com.clinic.Model;


import com.clinic.Enums.ClinicStatus;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Entity
@Table(name = "Clinic_Expert",
        indexes = {
                @Index(name = "inx_clinic_code", columnList = "clinic_code"),
                @Index(name = "inx_clinic_email", columnList = "clinic_email"),
                @Index(name = "idx_clinic_id", columnList = "clinic_id"),
                @Index(name = "idx_clinic_contact", columnList = "clinic_contact")
        }
)
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "clinic_id", updatable = false, nullable = false)
    @Hidden
    private UUID clinicId;

    @NotBlank
    @Column(name = "clinic_code")
    private String clinicCode;

    @NotBlank(message = "Clinic name cannot be blank")
    @Column(name = "clinic_name")
    private String clinicName;

    @NotNull(message = "Clinic status is required")
    @Column(name = "clinic_status", nullable = false)
    private ClinicStatus clinicStatus;

    @NotBlank
    @Column(name = "clinic_contact")
    private String clinicContact;

    @NotBlank
    @Email
    @Column(name = "clinic_email")
    private String clinicEmail;

    @NotBlank
    @Column(name = "clinic_password")
    private String clinicPassword;

    @NotBlank
    @Column(name = "clinic_address")
    private String clinicAddress;

    @NotBlank
    @Column(name = "clinic_city")
    private String clinicCity;

    @NotBlank
    @Column(name = "clinic_state")
    private String clinicState;

    @NotBlank
    @Column(name = "clinic_pin_code")
    private String clinicPinCode;

    @NotBlank
    @Column(name = "clinic_time_zone")
    private String clinicTimezone;

    @NotBlank
    @Column(name = "clinic_opening_time")
    private String clinicOpeningTime;

    @NotBlank
    @Column(name = "clinic_closing_time")
    private String clinicClosingTime;

    @NotBlank
    @Column(name = "clinic_logo")
    private String ClinicLogo;

    @NotBlank
    @Column(name = "clinic_subscription_plan")
    private String clinicSubscriptionPlan;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @NotBlank
    @Column(name = "create_by")
    @Hidden
    private String createBy;

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

    @PreUpdate
    public void preUpdate() {
        System.out.println("===Before update===");
    }

    @PostUpdate
    public void postUpdate() {
        System.out.println("===After Update===");
    }

    @PostLoad
    public void postLoad() {
        System.out.println("===Before Load Data ====");
    }

    @PreRemove
    public void preRemove() {
        System.out.println("===Before Remove Data===");
    }

    @PostRemove
    public void postRemove() {
        System.out.println("===After Remove===");
    }

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "clinic_roles",
            joinColumns = @JoinColumn(name = "clinicId", referencedColumnName = "clinic_id"),
            inverseJoinColumns = @JoinColumn(name = "roleName", referencedColumnName = "role_name")
    )
    private Set<Roles> roles;

}
