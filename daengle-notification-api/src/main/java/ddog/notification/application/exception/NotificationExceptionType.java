package ddog.notification.application.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum NotificationExceptionType {
    ALERT_CAN_NOT(HttpStatus.BAD_REQUEST, 7000, "알림을 전송할 수 없음"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 7001, "사용자를 찾을 수 없음"),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, 7002, "알림을 찾을 수 없음"),
    NOTIFICATION_CAN_NOT_DELETE(HttpStatus.BAD_REQUEST, 7003, "알림을 확인할 수 없음");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
