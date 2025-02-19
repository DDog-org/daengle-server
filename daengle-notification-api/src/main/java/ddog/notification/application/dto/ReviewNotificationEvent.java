package ddog.notification.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewNotificationEvent extends MessagePayload {
    private String revieweeName;
    private String revieweePhoneNumber;
    private String reviewContent;
}