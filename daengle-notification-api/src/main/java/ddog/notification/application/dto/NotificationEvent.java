package ddog.notification.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationEvent {
    private String eventType;
    private String customerName;
    private String customerPhoneNumber;
    private String reviewerName;
    private String revieweePhoneNumber;
}
