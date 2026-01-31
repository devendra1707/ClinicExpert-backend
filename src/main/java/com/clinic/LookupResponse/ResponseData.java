package com.clinic.LookupResponse;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ResponseData {

    // ===== COMMON ROLE =====
    private String roleName;

    // ===== CLINIC =====
    private String clinic_email;
    private String clinic_contact;
    private String clinic_status;
    private String clinic_address;
    private String clinic_city;
    private String clinic_state;
    private String clinic_pin_code;
    private String clinic_time_zone;
    private String clinicOpeningTime;
    private String clinicClosingTime;
    private String clinicSubscriptionPlan;
    private String clinicLogo;

    // ===== DOCTOR =====
    private String doctor_email;
    private String doctor_contact;
    private String doctor_status;
    private String doctor_specialization;
    private String doctor_qualification;
    private String doctor_experience;
    private String doctor_available_from;
    private String doctor_available_to;
    private String doctor_clinic_name;

    // ===== PATIENT =====
    private String patient_email;
    private String patient_contact;
    private String patient_status;
    private String patient_gender;
    private String patient_blood_group;
    private String patient_city;

    // ===== Factory Methods =====

    public static ResponseData forClinic(
            String email,
            String contact,
            String status,
            String address,
            String city,
            String state,
            String pinCode,
            String timeZone,
            String openingTime,
            String closingTime,
            String subscriptionPlan,
            String logo
    ) {
        ResponseData r = new ResponseData();
        r.setRoleName("ADMIN");
        r.setClinic_email(email);
        r.setClinic_contact(contact);
        r.setClinic_status(status);
        r.setClinic_address(address);
        r.setClinic_city(city);
        r.setClinic_state(state);
        r.setClinic_pin_code(pinCode);
        r.setClinic_time_zone(timeZone);
        r.setClinicOpeningTime(openingTime);
        r.setClinicClosingTime(closingTime);
        r.setClinicSubscriptionPlan(subscriptionPlan);
        r.setClinicLogo(logo);
        return r;
    }

    public static ResponseData forDoctor(
            String email,
            String contact,
            String status,
            String clinicName,
            String specialization,
            String qualification,
            String experience,
            String availableFrom,
            String availableTo
    ) {
        ResponseData r = new ResponseData();
        r.setRoleName("DOCTOR");
        r.setDoctor_email(email);
        r.setDoctor_contact(contact);
        r.setDoctor_status(status);
        r.setDoctor_clinic_name(clinicName);
        r.setDoctor_specialization(specialization);
        r.setDoctor_qualification(qualification);
        r.setDoctor_experience(experience);
        r.setDoctor_available_from(availableFrom);
        r.setDoctor_available_to(availableTo);
        return r;
    }

    public static ResponseData forPatient(
            String email,
            String contact,
            String status,
            String gender,
            String bloodGroup,
            String city

    ) {
        ResponseData r = new ResponseData();
        r.setRoleName("PATIENT");
        r.setPatient_email(email);
        r.setPatient_contact(contact);
        r.setPatient_status(status);
        r.setPatient_gender(gender);
        r.setPatient_blood_group(bloodGroup);
        r.setPatient_city(city);
        return r;
    }
}


