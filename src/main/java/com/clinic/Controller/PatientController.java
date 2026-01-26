package com.clinic.Controller;

import com.clinic.Model.Patient;
import com.clinic.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/clinic/{clinicId}/patients")
    @PreAuthorize("hasRole('Doctor') or hasRole('Admin')")
    public ResponseEntity<Patient> savePatient(@PathVariable UUID clinicId, @RequestBody Patient patient) {
        Patient savedPatient = patientService.savePatient(clinicId,patient);
        return ResponseEntity.ok(savedPatient);
    }

    // ===== Get All Patients =====
    @GetMapping("/all")
    @PreAuthorize("hasRole('Doctor') or hasRole('Admin')")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    // ===== Get Patient by ID (optional) =====
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Doctor') or hasRole('Admin')")
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") UUID id) {
        Patient patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }
}
