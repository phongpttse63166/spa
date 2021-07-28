package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import swp490.spa.entities.Notification;
import swp490.spa.entities.Role;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUser_IdAndRole(Integer customerId, Role role);
}
