package ddog.notification.application.adapter;

import ddog.notification.application.port.ClientConnect;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientConnector implements ClientConnect {

    private final ConcurrentHashMap<Long, SseEmitter> ssemitters = new ConcurrentHashMap<>();

    public SseEmitter toConnectClient(Long userId) {
        SseEmitter emitter = new SseEmitter(36000000L);
        ssemitters.put(userId, emitter);

        try {
            emitter.send("SSE 연결 완료");
        } catch (IOException e) {
            e.printStackTrace();
        }

        emitter.onTimeout(() -> {
            System.out.println("타임아웃 발생, 자동 재연결");
            ssemitters.remove(userId);
            toConnectClient(userId); 
        });

        emitter.onCompletion(() -> {
            ssemitters.remove(userId);
        });

        return emitter;
    }

    public void sendNotificationToUser(Long receiverId, String message) throws IOException {
        if (isUserConnected(receiverId))  {
            SseEmitter emitter = ssemitters.get(receiverId);
            emitter.send(message);
        }
    }

    public boolean isUserConnected(Long userId) {
        return ssemitters.containsKey(userId);
    }

}
