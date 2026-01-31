package com.clinic.LookupResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseIdentifier {

    // ===== CLINIC (ADMIN) =====
    private UUID clinic_id;
    private String clinic_code;
    private String clinic_name;

    // ===== DOCTOR =====
    private UUID doctor_id;
    private String doctor_full_name;

    // ===== PATIENT =====
    private UUID patient_id;
    private String patient_name;

    // =========Common
    private String token;

    public static ResponseIdentifier forClinic(
            UUID clinicId,
            String clinicCode,
            String clinicName,
            String token
    ) {
        ResponseIdentifier r = new ResponseIdentifier();
        r.setClinic_id(clinicId);
        r.setClinic_code(clinicCode);
        r.setClinic_name(clinicName);
        r.setToken(token);
        return r;
    }

    public static ResponseIdentifier forDoctor(
            UUID doctorId,
            String doctorName,
            String token
    ) {
        ResponseIdentifier r = new ResponseIdentifier();
        r.setDoctor_id(doctorId);
        r.setDoctor_full_name(doctorName);
        r.setToken(token);
        return r;
    }

    public static ResponseIdentifier forPatient(
            UUID patientId,
            String patientName,
            String token
    ) {
        ResponseIdentifier r = new ResponseIdentifier();
        r.setPatient_id(patientId);
        r.setPatient_name(patientName);
        r.setToken(token);
        return r;
    }

    public static ResponseIdentifier forClinic(
            UUID clinicId,
            String clinicCode,
            String clinicName
    ) {
        ResponseIdentifier r = new ResponseIdentifier();
        r.setClinic_id(clinicId);
        r.setClinic_code(clinicCode);
        r.setClinic_name(clinicName);
        return r;
    }



}
