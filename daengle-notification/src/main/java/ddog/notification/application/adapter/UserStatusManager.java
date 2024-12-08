package ddog.notification.application.adapter;

import ddog.notification.application.port.UserStatusPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStatusManager implements UserStatusPersist {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean isUserLoggedIn(Long userId) {
        return redisTemplate.hasKey("user_loggedIn:" + userId);
    }

    @Override
    public void setUserLogIn(Long userId) {
        redisTemplate.opsForValue().set("user_loggedIn:" + userId, "true");
    }

    @Override
    public void setUserLogOut(Long userId) {
        redisTemplate.delete("user_loggedIn:" + userId);
    }
}
