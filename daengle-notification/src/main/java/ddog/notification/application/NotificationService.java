package ddog.notification.application;

import ddog.domain.notification.Notification;
import ddog.domain.notification.enums.NotifyType;
import ddog.notification.application.exception.NotificationException;
import ddog.notification.application.exception.NotificationExceptionType;
import ddog.notification.application.port.ClientConnect;
import ddog.notification.application.port.UserStatusPersist;
import ddog.persistence.mysql.port.NotificationPersist;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
public class NotificationService {

    private final ClientConnect clientConnect;
    private final UserStatusPersist userStatusPersist;
    private final NotificationPersist notificationPersist;

    public NotificationService(ClientConnect clientConnect,
                               UserStatusPersist userStatusPersist,
                               NotificationPersist notificationPersist) {
        this.clientConnect = clientConnect;
        this.userStatusPersist = userStatusPersist;
        this.notificationPersist = notificationPersist;
    }

    public SseEmitter connectClient(Long userId) {
        return clientConnect.toConnectClient(userId);
    }

    public void sendNotificationToUser(Long receiverId, NotifyType notifyType, String message) {
        try {
            if (receiverId == null || message == null || notifyType == null) {
                throw new NotificationException(NotificationExceptionType.ALERT_CAN_NOT, "알림 전송 실패");
            }

            if (clientConnect.isUserConnected(receiverId)) {
                clientConnect.sendNotificationToUser(receiverId, message);
            } else if (userStatusPersist.isUserLoggedIn(receiverId)) {
                clientConnect.toConnectClient(receiverId);
                clientConnect.sendNotificationToUser(receiverId, message);
            } else {
                Notification notification = Notification.builder()
                        .userId(receiverId)
                        .message(message)
                        .notifyType(notifyType)
                        .build();

                notificationPersist.saveNotificationWithLogoutUser(notification);
            }
        } catch (IOException e) {
            throw new NotificationException(NotificationExceptionType.ALERT_CAN_NOT, e.getMessage());
        }
    }

    public List<Notification> getAllNotificationsByUserId(Long userId) {
        if (userId == null) {
            throw new NotificationException(NotificationExceptionType.ALERT_CAN_NOT, "유저 정보 가져오지 못함");
        }

        return notificationPersist.findNotificationsByUserId(userId);
    }
}
