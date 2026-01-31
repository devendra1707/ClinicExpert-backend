package com.clinic.Service;

import com.clinic.Email.EmailNotification;
import com.clinic.Enums.ClinicStatus;
import com.clinic.Exception.UsernameNotFoundException;
import com.clinic.LookupResponse.*;
import com.clinic.Model.Clinic;
import com.clinic.Model.Doctor;
import com.clinic.Model.Patient;
import com.clinic.Model.Roles;
import com.clinic.Repository.ClinicExpertRepository;
import com.clinic.Repository.DoctorRepository;
import com.clinic.Repository.PatientRepository;
import com.clinic.Request.ForgetPasswordRequest;
import com.clinic.Request.JwtRequest;
import com.clinic.Request.ResetPasswordRequest;
import com.clinic.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements UserDetailsService {

    private final ClinicExpertRepository clinicExpertRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotification emailNotification;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public RootPostResponse createJwtToken(JwtRequest jwtRequest) {
        log.info("Creating JWT Token");
        String clinicEmail = jwtRequest.getClinicEmail();
        String clinicPassword = jwtRequest.getClinicPassword();

        if (clinicPassword == null || clinicPassword.isBlank()) {
            log.error("Password is missing");
            throw new BadCredentialsException("Password is required.");
        }

        if ((clinicEmail == null || clinicEmail.isBlank())) {
            log.error("Email and Contact both are missing");
            throw new UsernameNotFoundException("Email or Contact number is required.");
        }

        validateLoginInput(clinicEmail, clinicPassword);
        Object user = findUserByEmailOrContact(clinicEmail);
        if (user == null) {
            log.error("Authentication failed: User not found");
            throw new UsernameNotFoundException("Invalid email/contact or password.");
        }
        return switch (user) {
            case Clinic clinic -> handleClinicLogin(clinic, clinicPassword);
            case Doctor doctor -> handleDoctorLogin(doctor, clinicPassword);
            case Patient patient -> handlePatientLogin(patient, clinicPassword);
            default -> {log.error("Unknown user type");
                throw new IllegalStateException("Unknown user type found");
            }
        };

    }

    private void validateLoginInput(String email, String password) {
        if (password == null || password.isBlank()) {
            log.error("Password is missing");
            throw new BadCredentialsException("Password is required.");
        }

        if ((email == null || email.isBlank())) {
            log.error("Email and Contact both are missing");
            throw new UsernameNotFoundException("Email or Contact number is required.");
        }
    }

    private RootPostResponse handleClinicLogin(Clinic clinic, String password) {
        validateClinicStatus(clinic);
        authenticate(clinic.getClinicEmail(), password);

        String token = jwtUtil.generateToken(loadUserByUsername(clinic.getClinicEmail()));
        String roleNames = clinic.getRoles().isEmpty() ? "ADMIN" : clinic.getRoles().iterator().next().getRoleName();
        ResponseIdentifier identifier =
                ResponseIdentifier.forClinic(
                        clinic.getClinicId(),
                        clinic.getClinicCode(),
                        clinic.getClinicName(),
                        token
                );

        ResponseData data = new ResponseData();
        data.setRoleName(roleNames);
        data.setClinic_email(clinic.getClinicEmail());
        data.setClinic_contact(clinic.getClinicContact());
        data.setClinic_status(clinic.getClinicStatus().toString());
        data.setClinic_address(clinic.getClinicAddress());
        data.setClinic_city(clinic.getClinicCity());
        data.setClinic_state(clinic.getClinicState());
        data.setClinic_pin_code(clinic.getClinicPinCode());
        data.setClinic_time_zone(clinic.getClinicTimezone());
        data.setClinicOpeningTime(clinic.getClinicOpeningTime());
        data.setClinicClosingTime(clinic.getClinicClosingTime());
        data.setClinicSubscriptionPlan(clinic.getClinicSubscriptionPlan());
        data.setClinicLogo(clinic.getClinicLogo());

        return new RootPostResponse(
                identifier,
                new ResponseDatas(data),
                new ResponseStatus("200 OK", "Clinic login successful")
        );
    }

    private RootPostResponse handleDoctorLogin(Doctor doctor, String password) {

        UserDetails userDetails = loadUserByUsername(doctor.getDoctorEmail());
        String token = jwtUtil.generateToken(userDetails);
        String roleName = doctor.getRoles().isEmpty() ? "DOCTOR" : doctor.getRoles().iterator().next().getRoleName();
        ResponseIdentifier identifier =
                ResponseIdentifier.forDoctor(
                        doctor.getDoctorId(),
                        doctor.getFullName(),
                        token
                );

        ResponseData responseData = ResponseData.forDoctor(
                doctor.getDoctorEmail(),
                doctor.getDoctorContact(),
                doctor.getStatus().toString(),
                doctor.getClinic().getClinicName(),
                doctor.getDoctorSpecialization(),
                doctor.getDoctorQualification(),
                doctor.getDoctorExperience(),
                doctor.getDoctorAvailableFrom(),
                doctor.getDoctorAvailableTo()
        );
        responseData.setRoleName(roleName);

        log.info("Doctor login successful: {}", doctor.getDoctorEmail());

        return new RootPostResponse(
                identifier,
                new ResponseDatas(responseData),
                new ResponseStatus("200 OK", "Doctor login successful")
        );
    }

    private RootPostResponse handlePatientLogin(Patient patient, String password) {

        authenticate(patient.getPatientEmail(), password);

        UserDetails userDetails = loadUserByUsername(patient.getPatientEmail());
        String token = jwtUtil.generateToken(userDetails);
        String roleName = patient.getRoles().isEmpty() ? "PATIENT" : patient.getRoles().iterator().next().getRoleName();
        ResponseIdentifier identifier =
                ResponseIdentifier.forPatient(
                        patient.getPatientId(),
                        patient.getFullName(),
                        token
                );

        ResponseData responseData = ResponseData.forPatient(
                patient.getPatientEmail(),
                patient.getContactNumber(),
                patient.getPatientStatus().toString(),
                patient.getGender(),
                patient.getBloodGroup(),
                patient.getCity()
        );
        responseData.setRoleName(roleName);

        log.info("Patient login successful: {}", patient.getPatientEmail());

        return new RootPostResponse(
                identifier,
                new ResponseDatas(responseData),
                new ResponseStatus("200 OK", "Patient login successful")
        );
    }

    /**
     * FORGET PASSWORD - Step 1: Generate reset token and send email
     */
//    public RootPostResponse forgetPassword(ForgetPasswordRequest request) {
//        log.info("Forget password request received");
//
//        String clinicEmail = request.getClinicEmail();
//        String clinicContact = request.getClinicContact();
//
//        if ((clinicEmail == null || clinicEmail.isBlank()) &&
//                (clinicContact == null || clinicContact.isBlank())) {
//            throw new UsernameNotFoundException("Email or Contact number is required.");
//        }
//
//        Object user = findUserByEmailOrContact(clinicEmail, clinicContact);
//
//        if (clinic == null) {
//            throw new UsernameNotFoundException("No account found with provided email/contact.");
//        }
//
//        // Generate reset token (valid for 15 minutes)
//        String resetToken = UUID.randomUUID().toString();
//        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);
//
//        switch (user) {
//            case Clinic clinic -> {
//                clinic.setResetToken(resetToken);
//                clinic.setResetTokenExpiry(expiry);
//                clinicExpertRepository.save(clinic);
//
//                sendResetEmail(clinic.getClinicEmail(), clinic.getClinicName(), resetToken);
//            }
//            case Doctor doctor -> {
//                doctor.setResetToken(resetToken);
//                doctor.setResetTokenExpiry(expiry);
//                doctorRepository.save(doctor);
//
//                sendResetEmail(doctor.getDoctorEmail(), doctor.getFullName(), resetToken);
//            }
//            case Patient patient -> {
//                patient.setResetToken(resetToken);
//                patient.setResetTokenExpiry(expiry);
//                patientRepository.save(patient);
//
//                sendResetEmail(patient.getPatientEmail(), patient.getFullName(), resetToken);
//            }
//            default -> {
//                log.error("Unknown user type");
//                throw new IllegalStateException("Unknown user type found");
//            }
//        }
//
//        ResponseStatus status = new ResponseStatus(
//                "200 OK",
//                "Password reset link has been sent to your email/contact."
//        );
//
//        log.info("Password reset token generated for identifier: {}", email != null ? email : contact);
//        return new RootPostResponse(null, null, status);
//    }

    /**
     * RESET PASSWORD - Step 2: Validate token and update password
     */
    public RootPostResponse resetPassword(ResetPasswordRequest request) {
        log.info("Reset password request received");

        String resetToken = request.getResetToken();
        String newPassword = request.getNewPassword();

        if (resetToken == null || resetToken.isBlank()) {
            throw new BadCredentialsException("Reset token is required.");
        }

        if (newPassword == null || newPassword.length() < 8) {
            throw new BadCredentialsException("Password must be at least 8 characters long.");
        }

        // Find clinic by reset token
        Clinic clinic = clinicExpertRepository.findByResetToken(resetToken);

        if (clinic == null) {
            log.error("Invalid reset token: {}", resetToken);
            throw new BadCredentialsException("Invalid reset token.");
        }

        // Check if token is expired
        if (clinic.getResetTokenExpiry() == null ||
                clinic.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            log.error("Expired reset token for clinic: {}", clinic.getClinicEmail());
            throw new BadCredentialsException("Reset token has expired. Please request a new password reset link.");
        }

        // Update password
        clinic.setClinicPassword(passwordEncoder.encode(newPassword));

        // Clear reset token
        clinic.setResetToken(null);
        clinic.setResetTokenExpiry(null);

        // Save updated clinic
        clinicExpertRepository.save(clinic);

        // Send confirmation email
        String confirmationSubject = "Password Reset Successful - Clinic Experts";
        String confirmationBody = buildPasswordResetConfirmationEmail(clinic.getClinicName());

        try {
            emailNotification.mailSender(confirmationSubject, confirmationBody, clinic.getClinicEmail());
            log.info("Password reset confirmation email sent to: {}", clinic.getClinicEmail());
        } catch (Exception e) {
            log.warn("Failed to send confirmation email: {}", e.getMessage());
        }

        ResponseStatus status = new ResponseStatus(
                "200 OK",
                "Password has been reset successfully. Please login with your new password."
        );

        log.info("Password reset successful for: {}", clinic.getClinicEmail());
        return new RootPostResponse(null, null, status);
    }

    private void validateClinicStatus(Clinic clinic) {
        switch (clinic.getClinicStatus()) {
            case ACTIVE -> log.info("Clinic is active: {}", clinic.getClinicName());
            case INACTIVE -> {
                log.warn("Clinic is inactive: {}", clinic.getClinicName());
                throw new DisabledException("Your account is inactive. Please contact support.");
            }
            case SUSPENDED -> {
                log.warn("Clinic is suspended: {}", clinic.getClinicName());
                throw new DisabledException("Your account is suspended. Please contact support.");
            }
            case CLOSED -> {
                log.warn("Clinic is closed: {}", clinic.getClinicName());
                throw new DisabledException("Your account is closed. Please contact support.");
            }
            default -> throw new RuntimeException("Unknown clinic status.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        Object user = findUserByEmailOrContact(username);

        if (user == null) {
            log.error("User not found with email/contact: {}", username);
            throw new UsernameNotFoundException("Invalid credentials.");
        }

        // Use switch pattern matching to handle each user type
        return switch (user) {
            case Clinic clinic -> {
                log.info("User found as Clinic: {}", clinic.getClinicName());
                yield new org.springframework.security.core.userdetails.User(
                        clinic.getClinicEmail(),
                        clinic.getClinicPassword(),
                        getAuthorities(clinic)
                );
            }
            case Doctor doctor -> {
                log.info("User found as Doctor: {}", doctor.getDoctorEmail());
                yield new org.springframework.security.core.userdetails.User(
                        doctor.getDoctorEmail(),
                        doctor.getDoctorPassword(),
                        new HashSet<>() // Or assign roles if available
                );
            }
            case Patient patient -> {
                log.info("User found as Patient: {}", patient.getPatientEmail());
                yield new org.springframework.security.core.userdetails.User(
                        patient.getPatientEmail(),
                        patient.getPatientPassword(),
                        new HashSet<>() // Or assign roles if available
                );
            }
            default -> {
                log.error("Unknown user type for username: {}", username);
                throw new IllegalStateException("Unknown user type found");
            }
        };
    }

    public Set<SimpleGrantedAuthority> getAuthorities(Clinic clinic) {
        log.info("Fetching roles for clinic: {}", clinic.getClinicName());
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        clinic.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        });
        return authorities;
    }

    private void authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            log.info("Authentication successful for: {}", email);
        } catch (BadCredentialsException e) {
            log.error("Bad credentials for: {}", email);
            throw new BadCredentialsException("Incorrect password. Please try again.");
        } catch (DisabledException e) {
            log.error("Account disabled for: {}", email);
            throw e;
        }
    }

    public Object findUserByEmailOrContact(String identifier) {

        if (identifier == null || identifier.isBlank()) {
            log.warn("Login attempt with empty identifier");
            return null;
        }

        identifier = identifier.trim();
        log.info("Searching for user with identifier: {}", identifier);

        // ===== Clinic =====
        Clinic clinic =
                clinicExpertRepository.findByClinicEmailOrClinicContact(identifier,identifier);
        if (clinic != null) {
            log.info("Clinic found: {}", clinic.getClinicName());
            return clinic;
        }

        // ===== Doctor =====
        Doctor doctor =
                doctorRepository.findByDoctorEmailOrDoctorContact(identifier,identifier);
        if (doctor != null) {
            log.info("Doctor found: {}", doctor.getDoctorContact());
            return doctor;
        }

        // ===== Patient =====
        Patient patient =
                patientRepository.findByPatientEmailOrContactNumber(identifier,identifier);
        if (patient != null) {
            log.info("Patient found: {}", patient.getFullName());
            return patient;
        }

        log.error("No user found in any table for identifier: {}", identifier);
        return null;
    }


    /**
     * Build HTML email for password reset
     */
    private String buildPasswordResetEmail(String clinicName, String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; border-radius: 5px; }
                        .button { display: inline-block; padding: 12px 30px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 30px; color: #777; font-size: 12px; }
                        .warning { background-color: #fff3cd; padding: 15px; border-left: 4px solid #ffc107; margin: 20px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Password Reset Request</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s,</h2>
                            <p>We received a request to reset your password for your Clinic Experts account.</p>
                            <p>Click the button below to reset your password:</p>
                            <a href="%s" class="button">Reset Password</a>
                            <p>Or copy and paste this link into your browser:</p>
                            <p style="word-break: break-all; color: #4CAF50;">%s</p>
                            <div class="warning">
                                <strong>⚠️ Important:</strong>
                                <ul>
                                    <li>This link will expire in 15 minutes</li>
                                    <li>If you didn't request this, please ignore this email</li>
                                    <li>Your password won't change until you create a new one</li>
                                </ul>
                            </div>
                        </div>
                        <div class="footer">
                            <p>© 2024 Clinic Experts. All rights reserved.</p>
                            <p>This is an automated email. Please do not reply.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(clinicName, resetLink, resetLink);
    }

    /**
     * Build HTML email for password reset confirmation
     */
    private String buildPasswordResetConfirmationEmail(String clinicName) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #28a745; color: white; padding: 20px; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 30px; border-radius: 5px; }
                        .success { background-color: #d4edda; padding: 15px; border-left: 4px solid #28a745; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 30px; color: #777; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>✓ Password Reset Successful</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s,</h2>
                            <div class="success">
                                <p><strong>Your password has been successfully reset!</strong></p>
                            </div>
                            <p>You can now login to your Clinic Experts account using your new password.</p>
                            <p>If you did not make this change, please contact our support team immediately.</p>
                            <p>For security reasons, we recommend:</p>
                            <ul>
                                <li>Using a strong, unique password</li>
                                <li>Not sharing your password with anyone</li>
                                <li>Changing your password regularly</li>
                            </ul>
                        </div>
                        <div class="footer">
                            <p>© 2024 Clinic Experts. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(clinicName);
    }
}