package com.clinic.Repository;

import com.clinic.Model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Doctor findByDoctorContact(String doctorContact);
    Doctor findByDoctorEmailOrDoctorContact(String email, String contact);
}
