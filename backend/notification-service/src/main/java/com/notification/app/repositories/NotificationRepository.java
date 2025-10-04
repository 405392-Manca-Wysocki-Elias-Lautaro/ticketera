package com.notification.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.notification.app.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
