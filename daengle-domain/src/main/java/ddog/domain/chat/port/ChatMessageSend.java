package ddog.domain.chat.port;

import ddog.domain.chat.ChatMessage;

public interface ChatMessageSend {
    void sendMessage(Long roomId, ChatMessage message);
}
