package com.clinic.Service;


import com.clinic.Model.Clinic;
import com.clinic.Repository.ClinicExpertRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class RegistrationService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final ClinicExpertRepository clinicRepo;

    public RegistrationService(BCryptPasswordEncoder passwordEncoder, ClinicExpertRepository clinicRepo) {
        this.passwordEncoder = passwordEncoder;
        this.clinicRepo = clinicRepo;
    }

    public Clinic registerClinic(Clinic clinic) {
        clinic.setCreateBy(clinic.getClinicName());
        if (clinic.getClinicPassword() == null || clinic.getClinicPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (clinicRepo.findByClinicEmail(clinic.getClinicEmail()) != null) {
            throw new IllegalStateException("Clinic email already exists");
        }

        if (clinicRepo.findByClinicContact(clinic.getClinicContact()) != null) {
            throw new IllegalStateException("Clinic contact already exists");
        }

        clinic.setClinicPassword(passwordEncoder.encode(clinic.getClinicPassword()));

        log.info("Clinic registered successfully: {}", clinic.getClinicEmail());
        return clinicRepo.save(clinic);
    }

}
