package ddog.domain.notification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotifyType {
    ESTIMATED("견적서 도착"),
    RESERVED("예약 완료"),
    RESERVED_DAY("예약 당일"),
    COMMUNICATE("채팅 및 비대면 답장"),
    COMPLETED("미용 및 진단 완료"),
    CERTIFIED("인증 완료"),
    VIDEO_CALL("비대면 상담"),
    MATCHING("매칭 완료"),
    REVIEWED("리뷰 등록"),
    CANCELED("예약 취소"),
    CALL("견적 요청"),
    NEARBY_CALL("위치 기반 알림");

    private String message;

}
