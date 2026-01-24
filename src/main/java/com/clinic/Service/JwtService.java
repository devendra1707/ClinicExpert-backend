package com.clinic.Service;

import com.clinic.Email.EmailNotification;
import com.clinic.Enums.ClinicStatus;
import com.clinic.Exception.UsernameNotFoundException;
import com.clinic.LookupResponse.*;
import com.clinic.Model.Clinic;
import com.clinic.Repository.ClinicExpertRepository;
import com.clinic.Request.ForgetPasswordRequest;
import com.clinic.Request.JwtRequest;
import com.clinic.Request.ResetPasswordRequest;
import com.clinic.Util.JwtUtil;
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
public class JwtService implements UserDetailsService {

    private final ClinicExpertRepository clinicExpertRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotification emailNotification;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public JwtService(@Lazy ClinicExpertRepository clinicExpertRepository,
                      @Lazy AuthenticationManager authenticationManager,
                      @Lazy JwtUtil jwtUtil,
                      @Lazy PasswordEncoder passwordEncoder,
                      @Lazy EmailNotification emailNotification) {
        this.clinicExpertRepository = clinicExpertRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.emailNotification = emailNotification;
    }

    public RootPostResponse createJwtToken(JwtRequest jwtRequest) {
        log.info("Creating JWT Token");
        String clinicEmail = jwtRequest.getClinicEmail();
        String clinicPassword = jwtRequest.getClinicPassword();
        String clinicContact = jwtRequest.getClinicContact();

        if (clinicPassword == null || clinicPassword.isBlank()) {
            log.error("Password is missing");
            throw new BadCredentialsException("Password is required.");
        }

        if ((clinicEmail == null || clinicEmail.isBlank()) &&
                (clinicContact == null || clinicContact.isBlank())) {
            log.error("Email and Contact both are missing");
            throw new UsernameNotFoundException("Email or Contact number is required.");
        }

        Clinic clinic = loginUsingClinicEmailORContact(clinicEmail, clinicContact);

        if (clinic == null) {
            log.error("No clinic found with provided credentials");
            throw new UsernameNotFoundException("Invalid email/contact or password.");
        }

        validateClinicStatus(clinic);
        authenticate(clinic.getClinicEmail(), clinicPassword);

        UserDetails userDetails = loadUserByUsername(clinic.getClinicEmail());
        String generateToken = jwtUtil.generateToken(userDetails);

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
                clinic.getClinicName(),
                generateToken
        );

        ResponseDatas responseDatas = new ResponseDatas(responseData);
        ResponseStatus status = new ResponseStatus("200 OK", "Login successful.");

        log.info("Login successful for clinic: {}", clinic.getClinicName());
        return new RootPostResponse(identifier, responseDatas, status);
    }

    /**
     * FORGET PASSWORD - Step 1: Generate reset token and send email
     */
    public RootPostResponse forgetPassword(ForgetPasswordRequest request) {
        log.info("Forget password request received");

        String clinicEmail = request.getClinicEmail();
        String clinicContact = request.getClinicContact();

        if ((clinicEmail == null || clinicEmail.isBlank()) &&
                (clinicContact == null || clinicContact.isBlank())) {
            throw new UsernameNotFoundException("Email or Contact number is required.");
        }

        Clinic clinic = loginUsingClinicEmailORContact(clinicEmail, clinicContact);

        if (clinic == null) {
            throw new UsernameNotFoundException("No account found with provided email/contact.");
        }

        // Generate reset token (valid for 15 minutes)
        String resetToken = UUID.randomUUID().toString();
        clinic.setResetToken(resetToken);
        clinic.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        // Save token to database
        clinicExpertRepository.save(clinic);

        // Create reset link
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        // Send email
        String emailSubject = "Password Reset Request - Clinic Experts";
        String emailBody = buildPasswordResetEmail(clinic.getClinicName(), resetLink);

        try {
            emailNotification.mailSender(emailSubject, emailBody, clinic.getClinicEmail());
            log.info("Password reset email sent to: {}", clinic.getClinicEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
            throw new RuntimeException("Failed to send password reset email. Please try again later.");
        }

        ResponseStatus status = new ResponseStatus(
                "200 OK",
                "Password reset link has been sent to your email: " + clinic.getClinicEmail()
        );

        log.info("Password reset token generated for: {}", clinic.getClinicEmail());
        return new RootPostResponse(null, null, status);
    }

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
        Clinic clinic = clinicExpertRepository.findByClinicEmail(username);
        if (clinic != null) {
            log.info("Clinic found: {}", clinic.getClinicName());
            return new User(clinic.getClinicEmail(), clinic.getClinicPassword(), getAuthorities(clinic));
        } else {
            log.error("Clinic not found with email: {}", username);
            throw new UsernameNotFoundException("Invalid credentials.");
        }
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

    public Clinic loginUsingClinicEmailORContact(String clinicEmail, String clinicContact) {
        Clinic clinic = null;

        if (clinicEmail != null && !clinicEmail.isBlank()) {
            clinic = clinicExpertRepository.findByClinicEmail(clinicEmail);
            if (clinic != null) {
                log.info("Clinic found by email");
                return clinic;
            }
        }

        if (clinicContact != null && !clinicContact.isBlank()) {
            clinic = clinicExpertRepository.findByClinicContact(clinicContact);
            if (clinic != null) {
                log.info("Clinic found by contact");
                return clinic;
            }
        }

        log.error("Clinic not found by email or contact");
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