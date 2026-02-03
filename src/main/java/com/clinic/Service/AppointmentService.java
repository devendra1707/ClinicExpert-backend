package com.clinic.Service;

import com.clinic.Model.Appointment;
import com.clinic.Model.Notification;
import com.clinic.Repository.AppointmentRepository;
import com.clinic.Repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;
    private final NotificationRepository notificationRepository;
    private final RealTimeNotificationService realTimeNotificationService;

    public Appointment bookAppointment(Appointment appointment) {
        Appointment savedAppointment = appointmentRepository.save(appointment);

        Notification notification = new Notification();
        notification.setDoctorId(savedAppointment.getDoctorId());
        notification.setClinicId(savedAppointment.getClinicId());
        notification.setTitle("New Appointment!");
        notification.setMessage(savedAppointment.getPatientName() + " has booked an appointment for " + savedAppointment.getTimeSlot() + ".");
        notificationRepository.save(notification);
        realTimeNotificationService.sendNotification(savedAppointment.getDoctorId(), notification.getMessage());
        return savedAppointment;
    }
}
