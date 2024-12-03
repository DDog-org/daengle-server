package ddog.groomer.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountExceptionType {
    INVALID_REQUEST_DATA_FORMAT(HttpStatus.BAD_REQUEST, 400, "데이터 형식 오류"),
    ACCOUNT_EXCEPTION_TYPE(HttpStatus.NOT_FOUND, 404, "유저를 찾을 수 없음"),
    NOT_FOUND_ACCOUNT(HttpStatus.NOT_FOUND, 404, "유저를 찾을 수 없음"),;

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
