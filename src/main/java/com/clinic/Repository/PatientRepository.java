package com.clinic.Repository;

import com.clinic.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Patient findByPatientEmailOrContactNumber(String email, String contact);
}
