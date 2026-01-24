package com.clinic.Repository;

import com.clinic.Model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClinicExpertRepository extends JpaRepository<Clinic, UUID> {

    Clinic findByClinicEmail(String email);
    Clinic findByClinicContact(String contact);
    Clinic findByResetToken(String resetToken);
}
