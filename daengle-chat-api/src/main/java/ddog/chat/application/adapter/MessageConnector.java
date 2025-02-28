package ddog.chat.application.adapter;

import ddog.domain.chat.ChatMessage;
import ddog.domain.chat.port.MessageConnectManage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageConnector implements MessageConnectManage {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(Long roomId, ChatMessage message) {
        messagingTemplate.convertAndSend("/sub/" + roomId, message);
    }
}
