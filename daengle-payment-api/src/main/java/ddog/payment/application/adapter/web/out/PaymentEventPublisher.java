package ddog.payment.application.adapter.web.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ddog.domain.event.port.EventPublish;
import ddog.payment.application.config.aws.AwsProperties;
import ddog.payment.application.exception.PaymentException;
import ddog.payment.application.exception.PaymentExceptionType;
import ddog.payment.application.dto.event.PaymentApplicationEvent;
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
        } catch (JsonProcessingException e) {
            throw new PaymentException(PaymentExceptionType.PAYMENT_MESSAGE_PARSING_ERROR);

        } catch (Exception e) {
            throw new PaymentException(PaymentExceptionType.PAYMENT_EVENT_PUBLISH_ERROR);
        }
    }
}
