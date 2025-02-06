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
            // 메시지 전체 로그 찍기
            log.info("수신된 메시지: {}", message.body());

            // "Message" 내부의 JSON 본문 추출
            JsonNode bodyNode = objectMapper.readTree(message.body());
            String messageContent = bodyNode.get("Message").asText();  // SNS 래핑 메시지 해제
            log.info("SNS 래핑 해제 후 메시지: {}", messageContent);

            // 실제 이벤트 메시지 파싱
            NotificationEvent notificationEvent = objectMapper.readValue(messageContent, NotificationEvent.class);
            log.info("파싱된 NotificationEvent: {}", notificationEvent);

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