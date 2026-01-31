package com.clinic.Controller;

import com.clinic.Model.Doctor;
import com.clinic.Service.DoctorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService){
        this.doctorService = doctorService;
    }

    @PostMapping("/{clinicId}/doctor")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public Doctor createDoctor(@PathVariable UUID clinicId, @RequestBody Doctor doctor){
        return doctorService.createDoctor(clinicId,doctor);
    }

    @PostMapping("/fetch/doctor/info")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public Doctor getDoctorInfo(@PathVariable String doctorContact){
        return doctorService.getDoctorInfo(doctorContact);
    }

    @GetMapping("/doctor/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public List<Doctor> doctorList(){
        return doctorService.getDoctorList();
    }
}
