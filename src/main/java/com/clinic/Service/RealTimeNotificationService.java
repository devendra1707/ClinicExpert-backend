package com.clinic.Service;

import com.clinic.Model.Notification;
import com.clinic.Repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RealTimeNotificationService {

    private final NotificationRepository notificationRepository;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String doctorId) {
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L); // 24 hours
        emitters.put(doctorId, emitter);

        emitter.onCompletion(() -> emitters.remove(doctorId));
        emitter.onTimeout(() -> emitters.remove(doctorId));
        return emitter;
    }

    public void sendNotification(String doctorId, String message) {
        if (emitters.containsKey(doctorId)) {
            try {
                emitters.get(doctorId).send(SseEmitter.event().name("APPOINTMENT").data(message));
            } catch (IOException e) {
                emitters.remove(doctorId);
            }
        }
    }

    @Transactional
    public void markAllAsRead(String doctorId) {
        List<Notification> unreadNotifications = notificationRepository.findByDoctorIdAndIsReadFalse(doctorId);
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
        });
        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void markSingleAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
