package ddog.notification.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationNotificationEvent extends MessagePayload {
    private String customerName;
    private String customerPhoneNumber;
    private String reservationStatus;
}