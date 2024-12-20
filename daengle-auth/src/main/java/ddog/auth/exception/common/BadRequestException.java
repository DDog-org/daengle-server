package ddog.auth.exception.common;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomRuntimeException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, null);
    }
}
