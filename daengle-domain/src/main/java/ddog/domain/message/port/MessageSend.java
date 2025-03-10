package ddog.domain.message.port;

public interface MessageSend {
    void send(MessageSendable message);
    void sendWithDelay(MessageSendable message);
}
