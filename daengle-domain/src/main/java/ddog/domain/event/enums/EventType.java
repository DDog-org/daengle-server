package ddog.domain.event.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {
    PAYMENT("결제 서비스"),
    REVIEW("리뷰 서비스"),
    RESERVATION("예약 서비스");

    private String message;
}
