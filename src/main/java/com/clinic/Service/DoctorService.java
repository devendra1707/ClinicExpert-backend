package com.clinic.Service;

import com.clinic.Enums.ClinicStatus;
import com.clinic.Exception.UsernameNotFoundException;
import com.clinic.Model.Clinic;
import com.clinic.Model.Doctor;
import com.clinic.Model.Roles;
import com.clinic.Repository.ClinicExpertRepository;
import com.clinic.Repository.DoctorRepository;
import com.clinic.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ClinicExpertRepository clinicExpertRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public Doctor createDoctor(UUID clinicId, Doctor doctor){
        Clinic clinicExpert = clinicExpertRepository.findById(clinicId).orElseThrow(() -> new UsernameNotFoundException("Clinic not found"));
        doctor.setClinic(clinicExpert);
        doctor.setStatus(ClinicStatus.ACTIVE);

        Roles doctorRole = roleRepository.findById("DOCTOR")
                .orElseGet(() -> {
                    Roles newRole = new Roles();
                    newRole.setRoleName("DOCTOR");
                    newRole.setRoleDescription("Doctor Role");
                    return roleRepository.save(newRole);
                });
        doctor.setRoles(Set.of(doctorRole));
        String password = doctor.getDoctorPassword();
        String encodePassword = passwordEncoder.encode(password);
        doctor.setDoctorPassword(encodePassword);
        return doctorRepository.save(doctor);
    }

    public Doctor getDoctorInfo(String doctorContact){
        return doctorRepository.findByDoctorContact(doctorContact);
    }

    public List<Doctor> getDoctorList(){
        return doctorRepository.findAll();
    }
}
