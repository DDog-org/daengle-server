package ddog.notification.application;

import ddog.notification.application.exception.NotificationException;
import ddog.notification.application.exception.NotificationExceptionType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.KakaoOption;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.*;

@Service
public class KakaoNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(KakaoNotificationService.class);

    private final DefaultMessageService messageService;

    @Value("${kakao.pfId}")
    private String kakaoPfid;

    @Value("${kakao.setFrom}")
    private String kakaoFrom;

    public KakaoNotificationService(@Value("${kakao.apiKey}") String kakaoApiKey,
                                    @Value("${kakao.apiSecretKey}") String kakaoApiSecretKey,
                                    @Value("${kakao.domain}") String kakaoDomain) {
        this.messageService = NurigoApp.INSTANCE.initialize(kakaoApiKey, kakaoApiSecretKey, kakaoDomain);
    }
    private void executeNotification(String userName, String userPhoneNumber, String template) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Void> future = executor.submit(() -> {
            sendOneTalk(userName, userPhoneNumber, template);
            return null;
        });

        try {
            future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new NotificationException(NotificationExceptionType.NOTIFICATION_TIMEOUT);
        } catch (Exception e) {
            throw new NotificationException(NotificationExceptionType.NOTIFICATION_RUNTIME_EXCEPTION);
        } finally {
            executor.shutdownNow();
        }
    }

    public void sendOneTalk(String userName, String userPhoneNumber, String template) {
        KakaoOption kakaoOption = new KakaoOption();
        kakaoOption.setDisableSms(true);

        kakaoOption.setPfId(kakaoPfid);
        kakaoOption.setTemplateId(template);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("#{사용자}", userName);
        kakaoOption.setVariables(variables);

        Message messageTosend = new Message();
        messageTosend.setFrom(kakaoFrom);
        messageTosend.setTo(userPhoneNumber);

        messageTosend.setKakaoOptions(kakaoOption);

        this.messageService.sendOne(
                new SingleMessageSendingRequest(messageTosend)
        );
    }

    @CircuitBreaker(name = "kakaoNotificationService", fallbackMethod = "fallbackSendOneTalk")
    public boolean sendOneMessage(String userName, String userPhoneNumber, String template) {
        try {
            executeNotification(userName, userPhoneNumber, template);
            return true;
        } catch (Exception e) {
            throw new NotificationException(NotificationExceptionType.NOTIFICATION_RUNTIME_EXCEPTION);
        }
    }

    public boolean fallbackSendOneMessage(String userName, String userPhoneNumber, String template, Throwable throwable) {
        return false;
    }
}