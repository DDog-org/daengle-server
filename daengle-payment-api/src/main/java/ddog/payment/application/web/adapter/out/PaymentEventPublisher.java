package ddog.payment.application.web.adapter.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ddog.domain.event.EventPublish;
import ddog.payment.application.config.aws.AwsProperties;
import ddog.payment.presentation.dto.PaymentApplicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher implements EventPublish<PaymentApplicationEvent> {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final AwsProperties awsProperties;

    @Override
    public void publishEvent(PaymentApplicationEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            String topicArn = awsProperties.getSnsPaymentTopicArn();

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();

            PublishResponse response = snsClient.publish(request);
            log.info("SNS 이벤트 발행 성공: Message ID = {}", response.messageId());
        } catch (JsonProcessingException e) {
            log.error("이벤트 직렬화 실패: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("SNS 이벤트 발행 실패: {}", e.getMessage(), e);
        }
    }
}
