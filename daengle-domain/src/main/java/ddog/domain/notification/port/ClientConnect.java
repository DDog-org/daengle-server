package ddog.domain.notification.port;

import java.io.IOException;

public interface ClientConnect<T> {
    T toConnectClient(Long userId);
    void sendNotificationToUser(Long userId, String message) throws IOException;
    boolean isUserConnected(Long userId);
}
