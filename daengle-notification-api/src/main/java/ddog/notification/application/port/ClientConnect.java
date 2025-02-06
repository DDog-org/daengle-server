package ddog.notification.application.port;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public interface ClientConnect {
    SseEmitter toConnectClient(Long userId);
    void sendNotificationToUser(Long userId, String message) throws IOException;
    boolean isUserConnected(Long userId);
}
