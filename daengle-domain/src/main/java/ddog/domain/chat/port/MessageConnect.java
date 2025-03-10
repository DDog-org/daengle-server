package ddog.domain.chat.port;

import ddog.domain.chat.ChatMessage;

public interface MessageConnect {
    void sendMessage(Long roomId, ChatMessage message);
}
