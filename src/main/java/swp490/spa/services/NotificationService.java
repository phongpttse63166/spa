package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Notification;
import swp490.spa.repositories.NotificationRepository;

@Service
public class NotificationService {
    @Autowired
    NotificationRepository notificationRepository;

    public Notification insertNewNotification(Notification notification) {
        return this.notificationRepository.save(notification);
    }
}
