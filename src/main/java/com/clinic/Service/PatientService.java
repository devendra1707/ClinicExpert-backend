package com.clinic.Service;


import com.clinic.Enums.ClinicStatus;
import com.clinic.Model.Clinic;
import com.clinic.Model.Patient;
import com.clinic.Model.Roles;
import com.clinic.Repository.ClinicExpertRepository;
import com.clinic.Repository.PatientRepository;
import com.clinic.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final ClinicExpertRepository clinicExpertRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // ===== Save Patient =====
    public Patient savePatient(UUID clinicId,  Patient patient) {
        Clinic clinic = clinicExpertRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Clinic not found"));
        Roles patientRole = roleRepository.findById("PATIENT")
                .orElseGet(() -> {
                    Roles newRole = new Roles();
                    newRole.setRoleName("PATIENT");
                    newRole.setRoleDescription("Patient Role");
                    return roleRepository.save(newRole);
                });

        // Assign role to patient
        patient.setClinic(clinic);
        patient.setRoles(Set.of(patientRole));
        patient.setPatientStatus(ClinicStatus.ACTIVE);
        String password = patient.getPatientPassword();
        String encodePassword = passwordEncoder.encode(password);
        patient.setPatientPassword(encodePassword);
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // ===== Get Patient by ID (optional) =====
    public Patient getPatientById(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
    }
}
