package com.clinic.Controller;

import com.clinic.LookupResponse.RootPostResponse;
import com.clinic.Request.ForgetPasswordRequest;
import com.clinic.Request.JwtRequest;
import com.clinic.Request.ResetPasswordRequest;
import com.clinic.Service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and Password Management APIs")
@CrossOrigin(origins = "*")
public class ClinicLogin {

    private final JwtService jwtService;

    public ClinicLogin(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Clinic Login", description = "Login with email/contact and password to get JWT token")
    public ResponseEntity<RootPostResponse> loginClinic(@Valid @RequestBody JwtRequest jwtRequest) {
        log.info("Login request received for: {}", jwtRequest.getClinicEmail());
        RootPostResponse response = jwtService.createJwtToken(jwtRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forget-password")
    @Operation(summary = "Forget Password", description = "Request password reset link via email")
    public ResponseEntity<RootPostResponse> forgetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        log.info("Forget password request received");
        RootPostResponse response = jwtService.forgetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Reset password using reset token")
    public ResponseEntity<RootPostResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Reset password request received");
        RootPostResponse response = jwtService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    @Operation(summary = "Health Check", description = "Check if authentication service is running")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Authentication service is running");
    }
}