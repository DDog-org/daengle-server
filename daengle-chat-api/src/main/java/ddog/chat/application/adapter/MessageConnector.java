package ddog.chat.application.adapter;

import ddog.domain.chat.ChatMessage;
import ddog.domain.chat.port.MessageConnect;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageConnector implements MessageConnect {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(Long roomId, ChatMessage message) {
        messagingTemplate.convertAndSend("/sub/" + roomId, message);
    }
}
