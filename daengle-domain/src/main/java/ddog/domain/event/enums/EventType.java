package ddog.domain.event.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {
    PAYMENT("결제 이벤트"),
    REVIEW("리뷰 이벤트"),
    RESERVATION("예약 이벤트");

    private String message;
}
