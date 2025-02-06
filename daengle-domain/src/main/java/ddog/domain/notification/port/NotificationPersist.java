package ddog.domain.notification.port;


import ddog.domain.notification.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationPersist {

    List<Notification> findNotificationsByUserId(Long userId);

    void saveNotificationWithLogoutUser(Notification notification);

    Optional<Notification> findNotificationById(Long notificationId);

    void deleteNotificationById(Long notificationId);
}
