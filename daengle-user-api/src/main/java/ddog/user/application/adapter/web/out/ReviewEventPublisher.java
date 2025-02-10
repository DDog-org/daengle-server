package ddog.user.application.adapter.web.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ddog.domain.event.port.EventPublish;
import ddog.user.application.config.AwsProperties;
import ddog.user.application.dto.event.ReviewEvent;
import ddog.user.application.exception.account.UserException;
import ddog.user.application.exception.account.UserExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventPublisher implements EventPublish<ReviewEvent> {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;
    private final AwsProperties awsProperties;

    @Override
    public void publishEvent(ReviewEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            String topicArn = awsProperties.getSnsUserTopicArn();

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build();

            PublishResponse response = snsClient.publish(request);
        } catch (JsonProcessingException e) {
            throw new UserException(UserExceptionType.REVIEW_MESSAGE_PARSING_ERROR);

        } catch (Exception e) {
            throw new UserException(UserExceptionType.REVIEW_EVENT_PUBLISH_ERROR);
        }
    }
}
