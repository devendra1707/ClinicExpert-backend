package com.clinic.Model;

import com.clinic.Enums.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_table")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointment_id")
    @Hidden
    private UUID appointmentId;

    @Column(name = "patient_name")
    private String patientName;

    @Column(name = "patient_contact")
    private String patientContact;

    @Column(name = "gender")
    private String gender;

    @Column(name = "clinic_id")
    private String clinicId;

    @Column(name = "doctor_id")
    private String doctorId;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "time_slot")
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", pattern = "HH:mm:ss", example = "10:30:00")
    private LocalTime timeSlot;

    @Column(name = "appointment_type")
    private String appointmentType;

    @Column(name = "patient_status")
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;
}
