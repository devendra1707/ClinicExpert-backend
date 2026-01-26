package com.clinic.Service;


import com.clinic.LookupResponse.*;
import com.clinic.Model.Clinic;
import com.clinic.Repository.ClinicExpertRepository;
import com.clinic.Request.ClinicContactRequest;
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

    public RootPostResponse GetClinicInfo(ClinicContactRequest request){
        Clinic clinic = clinicRepo.findByClinicContact(request.getClinicContact());
        ResponseData responseData = new ResponseData(
                clinic.getClinicEmail(),
                clinic.getClinicContact(),
                clinic.getClinicStatus().toString(),
                clinic.getClinicAddress(),
                clinic.getClinicCity(),
                clinic.getClinicState(),
                clinic.getClinicPinCode(),
                clinic.getClinicTimezone(),
                clinic.getClinicOpeningTime(),
                clinic.getClinicClosingTime(),
                clinic.getClinicSubscriptionPlan(),
                clinic.getClinicLogo()
        );

        ResponseIdentifier identifier = new ResponseIdentifier(
                clinic.getClinicId(),
                clinic.getClinicCode(),
                clinic.getClinicName()
        );

        ResponseDatas responseDatas = new ResponseDatas(responseData);
        ResponseStatus status = new ResponseStatus("200 OK", "Login successful.");

        log.info("Login successful for clinic: {}", clinic.getClinicName());
        return new RootPostResponse(identifier, responseDatas, status);
    }

}
