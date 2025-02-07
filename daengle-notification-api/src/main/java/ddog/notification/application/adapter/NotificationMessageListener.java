package ddog.notification.application.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ddog.domain.notification.port.NotificationMessageListen;
import ddog.notification.application.KakaoNotificationService;
import ddog.notification.application.dto.NotificationEvent;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.Message;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationMessageListener implements NotificationMessageListen<Message> {

    private final ObjectMapper objectMapper;
    private final KakaoNotificationService kakaoNotificationService;
    private final Environment environment;

    @Override
    @SqsListener(value = "NotificationQueue", factory = "sqsListenerContainerFactory")
    public void listen(Message message) {
        try {
            JsonNode bodyNode = objectMapper.readTree(message.body());
            String messageContent = bodyNode.get("Message").asText();

            NotificationEvent notificationEvent = objectMapper.readValue(messageContent, NotificationEvent.class);

            handleEvent(notificationEvent);

        } catch (Exception e) {
            log.error("알림 메시지 처리 실패: {}", message.body(), e);
        }
    }

    private void handleEvent(NotificationEvent event) {
        switch (event.getEventType()) {
            case "PAYMENT":
                kakaoNotificationService.sendOneTalk(
                        event.getCustomerName(),
                        event.getCustomerPhoneNumber(),
                        environment.getProperty("templateId.REVIEWED")
                );
                break;

            case "RESERVATION":
                kakaoNotificationService.sendOneTalk(
                        event.getCustomerName(),
                        event.getCustomerPhoneNumber(),
                        environment.getProperty("templateId.RESERVED")
                );
                break;

            case "REVIEW":
                kakaoNotificationService.sendOneTalk(
                        event.getReviewerName(),
                        event.getRevieweePhoneNumber(),
                        environment.getProperty("templateId.REVIEWED")
                );
                break;

            default:
                log.warn("알 수 없는 이벤트 타입: {}", event.getEventType());
        }
    }
}