package ddog.notification.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationResp {
    public Long id;
    public String message;
}
