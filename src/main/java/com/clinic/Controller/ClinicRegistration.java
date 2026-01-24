package com.clinic.Controller;


import com.clinic.Model.Clinic;
import com.clinic.Service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ClinicRegistration {

    @Autowired
    private RegistrationService registrationService;


    @PostMapping("/registration")
    public Clinic clinicRegistrationData(@RequestBody Clinic clinic){
        return registrationService.registerClinic(clinic);
    }
}
