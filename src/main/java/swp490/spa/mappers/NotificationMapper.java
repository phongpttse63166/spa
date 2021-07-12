package swp490.spa.mappers;

import org.mapstruct.Mapper;
import swp490.spa.dto.responses.NotificationResponse;
import swp490.spa.entities.Notification;

@Mapper
public interface NotificationMapper {
    NotificationResponse changeToNotificationResponse(Notification notification);
}
