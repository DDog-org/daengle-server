package ddog.user.application.exception.account;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum UserExceptionType {
    INVALID_REQUEST_DATA_FORMAT(HttpStatus.BAD_REQUEST, 400, "데이터 형식 오류"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "사용자가 존재하지 않음."),
    REVIEW_MESSAGE_PARSING_ERROR(HttpStatus.BAD_REQUEST, 1002, "리뷰 이벤트 메시지 발행 중 직렬화 에러 발생"),
    REVIEW_EVENT_PUBLISH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1003, "리뷰 이벤트 발행 중 알 수 없는 에러");

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
