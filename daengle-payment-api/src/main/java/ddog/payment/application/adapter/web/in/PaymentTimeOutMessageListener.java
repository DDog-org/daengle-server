package ddog.payment.application.adapter.web.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ddog.domain.message.port.MessageSend;
import ddog.domain.payment.port.PaymentTimeOutMessageListen;
import ddog.payment.application.PaymentService;
import ddog.payment.application.dto.message.PaymentTimeoutMessage;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sqs.model.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTimeOutMessageListener implements PaymentTimeOutMessageListen<Message> {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    private final MessageSend messageSend;

    //TODO 에러 로깅 슬랙 연동
    @Override
    @SqsListener(value = "PaymentTimeoutQ", factory = "sqsListenerContainerFactory")
    public void listen(Message message) {
        PaymentTimeoutMessage paymentTimeoutMessage = parseMessageBody(message);
        try {
            paymentService.refundPayment(paymentTimeoutMessage.getPaymentUid(), paymentTimeoutMessage.getOrderUid());

        } catch (Exception e) {
            messageSend.sendWithDelay(paymentTimeoutMessage);
        }
    }

    private PaymentTimeoutMessage parseMessageBody(Message message) {
        try {
            JsonNode bodyNode = objectMapper.readTree(message.body());

            String paymentUid = bodyNode.get("paymentUid").asText();
            String orderUid = bodyNode.get("orderUid").asText();
            Long estimateId = Long.valueOf(bodyNode.get("estimateId").asText());

            return PaymentTimeoutMessage.builder()
                    .paymentUid(paymentUid)
                    .orderUid(orderUid)
                    .estimateId(estimateId)
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse message: " + message, e);
        }
    }
}