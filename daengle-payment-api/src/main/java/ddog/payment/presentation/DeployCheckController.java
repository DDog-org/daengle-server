package ddog.payment.presentation;

import ddog.domain.message.port.MessageSend;
import ddog.payment.application.dto.message.PaymentTimeoutMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class DeployCheckController {
    private final MessageSend messageSend;
    private String deploymentTime;

    @PostConstruct
    public void init() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        deploymentTime = now.format(formatter);
    }

    @GetMapping("/test")
    public String test() {

        PaymentTimeoutMessage message = new PaymentTimeoutMessage("TEST_PAYMENT_UID", "TEST_ORDER_UID", -1L);
        messageSend.send(message);

        return "Hello Daengle World - DIFF -MULTI MODULE !!!! PAYMENT API  2트!" +
                " Made at: " + deploymentTime + "   CI/CD SUCCESS";
    }
}
