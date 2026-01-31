package com.clinic.Repository;

import com.clinic.Model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClinicExpertRepository extends JpaRepository<Clinic, UUID> {

    Clinic findByClinicEmailOrClinicContact(String email, String contact);
    Clinic findByClinicEmail(String clinicEmail);
    Clinic findByClinicContact(String clinicContact);
    Clinic findByResetToken(String resetToken);
}
