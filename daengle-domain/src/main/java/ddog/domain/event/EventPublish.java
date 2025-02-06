package ddog.domain.event;

public interface EventPublish<T> {
    void publishEvent(T event);
}
