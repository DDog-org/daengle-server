package ddog.payment.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/payment")
public class TestController {
    @GetMapping("/test")
    public String test() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        return "Hello Daengle World - MULTI MODULE !!!! PAYMENT API !" +
                " Made at: " + formattedDate + "   CI/CD SUCCESS";
    }
}
