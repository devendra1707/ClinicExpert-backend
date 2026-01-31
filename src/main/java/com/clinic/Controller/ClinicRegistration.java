package com.clinic.Controller;


import com.clinic.LookupResponse.RootPostResponse;
import com.clinic.Model.Clinic;
import com.clinic.Request.ClinicContactRequest;
import com.clinic.Service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class ClinicRegistration {

    @Autowired
    private RegistrationService registrationService;


    @PostMapping("/registration")
    public Clinic clinicRegistrationData(@RequestBody Clinic clinic){
        return registrationService.registerClinic(clinic);
    }

    @PostMapping("/getClinicInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public RootPostResponse getClinicInformation(@RequestBody ClinicContactRequest clinicContact){
        return registrationService.GetClinicInfo(clinicContact);
    }
}
