package ddog.auth.config.jwt;

import ddog.auth.exception.AuthException;
import ddog.auth.exception.AuthExceptionType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        /* 권한 없는 곳에 접근할 경우 403 에러 */
        throw new AuthException(AuthExceptionType.UNAVAILABLE_ROLE);
    }
}
