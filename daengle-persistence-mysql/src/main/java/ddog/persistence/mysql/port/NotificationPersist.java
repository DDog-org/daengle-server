package ddog.persistence.mysql.port;


import ddog.domain.notification.Notification;

import java.util.List;

public interface NotificationPersist {

    List<Notification> findNotificationsByUserId(Long userId);

    void saveNotificationWithLogoutUser(Notification notification);

    Notification findNotificationById(Long notificationId);

    void deleteNotificationById(Long notificationId);

}
