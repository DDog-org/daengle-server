package ddog.notification.application;

import ddog.domain.notification.Notification;
import ddog.domain.notification.enums.NotifyType;

import ddog.domain.notification.port.NotificationPersist;
import ddog.domain.user.port.UserPersist;
import ddog.notification.application.dto.NotificationResp;
import ddog.notification.application.exception.NotificationException;
import ddog.notification.application.exception.NotificationExceptionType;
import ddog.notification.application.port.ClientConnect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ClientConnect clientConnect;
    private final NotificationPersist notificationPersist;
    private final UserPersist userPersist;

    public SseEmitter connectClient(Long userId) {
        return clientConnect.toConnectClient(userId);
    }

    public void sendNotificationToUser(Long receiverId, NotifyType notifyType, String message) {
        try {
            if (clientConnect.isUserConnected(receiverId)) {
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
            throw new NotificationException(NotificationExceptionType.ALERT_CAN_NOT);
        }
    }

    public List<NotificationResp> findAllNotificationsBy(Long userId) {
        userPersist.findByAccountId(userId)
                .orElseThrow(() -> new NotificationException(NotificationExceptionType.USER_NOT_FOUND));

        return Optional.ofNullable(notificationPersist.findNotificationsByUserId(userId))
                .filter(notifications -> !notifications.isEmpty())
                .orElseThrow(() -> new NotificationException(NotificationExceptionType.NOTIFICATION_NOT_FOUND))
                .stream()
                .map(notification -> NotificationResp.builder()
                        .id(notification.getId())
                        .message(notification.getMessage())
                        .build())
                .collect(Collectors.toList());
    }

    public Map<String, Object> checkNotificationById(Long notificationId) {
        notificationPersist.findNotificationById(notificationId)
                .orElseThrow(() -> new NotificationException(NotificationExceptionType.NOTIFICATION_NOT_FOUND));

        notificationPersist.deleteNotificationById(notificationId);

        Map<String, Object> response = new HashMap<>();
        response.put("delete", true);
        return response;
    }
}