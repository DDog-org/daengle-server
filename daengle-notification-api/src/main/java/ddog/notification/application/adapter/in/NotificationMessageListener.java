package ddog.notification.application.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ddog.domain.notification.port.NotificationMessageListen;
import ddog.notification.application.KakaoNotificationService;
import ddog.notification.application.dto.MessagePayload;
import ddog.notification.application.dto.PaymentNotificationEvent;
import ddog.notification.application.dto.ReservationNotificationEvent;
import ddog.notification.application.dto.ReviewNotificationEvent;
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

            MessagePayload messagePayload = objectMapper.readValue(messageContent, MessagePayload.class);
            handleEvent(messagePayload, messageContent);

        } catch (Exception e) { //TODO 슬랙봇 연동
            log.error("알림 메시지 처리 실패: {}", message.body(), e);
        }
    }

    private void handleEvent(MessagePayload payload, String messageContent) {
        try {
            switch (payload.getEventType()) {
                case "PAYMENT":
                    PaymentNotificationEvent paymentEvent = objectMapper.readValue(messageContent, PaymentNotificationEvent.class);
                    kakaoNotificationService.sendOneTalk(
                            paymentEvent.getCustomerName(),
                            paymentEvent.getCustomerPhoneNumber(),
                            environment.getProperty("templateId.PAYMENT")
                    );
                    break;

                case "RESERVATION":
                    ReservationNotificationEvent reservationEvent = objectMapper.readValue(messageContent, ReservationNotificationEvent.class);
                    kakaoNotificationService.sendOneTalk(
                            reservationEvent.getCustomerName(),
                            reservationEvent.getCustomerPhoneNumber(),
                            environment.getProperty("templateId.RESERVED")
                    );
                    break;

                case "REVIEW":
                    ReviewNotificationEvent reviewEvent = objectMapper.readValue(messageContent, ReviewNotificationEvent.class);
                    kakaoNotificationService.sendOneTalk(
                            reviewEvent.getRevieweeName(),
                            reviewEvent.getRevieweePhoneNumber(),
                            environment.getProperty("templateId.REVIEWED")
                    );
                    break;

                default: //TODO 슬랙봇 연동
                    log.warn("알 수 없는 이벤트 타입: {}", payload.getEventType());
            }
        } catch (Exception e) { //TODO 슬랙봇 연동
            log.error("이벤트 처리 중 오류 발생: {}", payload.getEventType(), e);
        }
    }
}