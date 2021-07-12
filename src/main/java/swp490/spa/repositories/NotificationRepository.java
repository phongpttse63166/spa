package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import swp490.spa.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
