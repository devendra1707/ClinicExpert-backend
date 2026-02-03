package com.clinic.Controller;

import com.clinic.Model.Notification;
import com.clinic.Repository.NotificationRepository;
import com.clinic.Service.RealTimeNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final RealTimeNotificationService realTimeNotificationService;

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    @GetMapping("/doctor/{doctorId}")
    public List<Notification> getDoctorNotifications(@PathVariable String doctorId) {
        return notificationRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId);
    }

    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN') or hasRole('PATIENT')")
    @GetMapping("/count/{doctorId}")
    public long getUnreadCount(@PathVariable String doctorId) {
        return notificationRepository.countByDoctorIdAndIsReadFalse(doctorId);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/mark-all-read/{doctorId}")
    public ResponseEntity<Void> markAllRead(@PathVariable String doctorId) {
        realTimeNotificationService.markAllAsRead(doctorId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        realTimeNotificationService.markSingleAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/{doctorId}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable String doctorId) {
        return notificationRepository.findByDoctorIdAndIsReadFalse(doctorId);
    }

}

