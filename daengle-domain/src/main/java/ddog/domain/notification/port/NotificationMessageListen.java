package ddog.domain.notification.port;

public interface NotificationMessageListen<T> {
    void listen(T message);
}
