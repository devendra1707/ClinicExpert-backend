package com.clinic.Repository;

import com.clinic.Model.Notification;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDoctorIdOrderByCreatedAtDesc(String doctorId);
    long countByDoctorIdAndIsReadFalse(String doctorId);
    List<Notification> findByDoctorIdAndIsReadFalse(String doctorId);

}
