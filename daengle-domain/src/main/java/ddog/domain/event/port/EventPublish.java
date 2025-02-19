package ddog.domain.event.port;

public interface EventPublish<T> {
    void publishEvent(T event);
}
