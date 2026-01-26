package com.clinic.Controller;

import com.clinic.Model.Doctor;
import com.clinic.Service.DoctorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService){
        this.doctorService = doctorService;
    }

    @PostMapping("/{clinicId}/doctor")
    @PreAuthorize("hasRole('Admin')")
    public Doctor createDoctor(@PathVariable UUID clinicId, @RequestBody Doctor doctor){
        return doctorService.createDoctor(clinicId,doctor);
    }

    @PostMapping("/fetch/doctor/info")
    @PreAuthorize("hasRole('Admin','Doctor')")
    public Doctor getDoctorInfo(@PathVariable String doctorContact){
        return doctorService.getDoctorInfo(doctorContact);
    }
}
