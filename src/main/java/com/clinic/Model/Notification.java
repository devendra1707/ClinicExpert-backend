package com.clinic.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_id")
    private String doctorId;

    @Column(name = "clinic_id")
    private String clinicId;

    private String title;

    private String message;

    private boolean isRead = false;

    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}
