package ddog.persistence.queue.adapter;

import ddog.domain.message.port.MessageSend;
import ddog.domain.message.port.MessageSendable;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSender implements MessageSend {

    private final SqsTemplate sqsTemplate;
    public static final String SSQ_NAME = "PaymentTimeoutQ";

    private static final int DELAY_SECONDS = 60;

    @Override
    public void send(MessageSendable messageSendable) {
        String message = messageSendable.toString();

        sqsTemplate.send(to -> to
                .queue(SSQ_NAME)
                .payload(message));
    }

    @Override
    public void sendWithDelay(MessageSendable messageSendable) {
        String message = messageSendable.toString();

        sqsTemplate.send(to -> to
                .queue(SSQ_NAME)
                .payload(message)
                .delaySeconds(DELAY_SECONDS));  // Delay 설정
    }
}
