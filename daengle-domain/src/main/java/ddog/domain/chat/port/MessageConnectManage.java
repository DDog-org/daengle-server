package ddog.domain.chat.port;

import ddog.domain.chat.ChatMessage;

public interface MessageConnectManage {
    void sendMessage(Long roomId, ChatMessage message);
}
