package ddog.auth.interceptor;

import ddog.auth.config.jwt.JwtTokenProvider;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.AuthException;
import ddog.auth.exception.AuthExceptionType;
import ddog.domain.account.Role;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    public static final String PAYLOAD_ATTRIBUTE = "auth_payload";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = resolveToken(request);

        /* 토큰이 없는 경우는 비회원으로 간주 */
        if (token == null) {
            request.setAttribute(PAYLOAD_ATTRIBUTE, new PayloadDto(null, null, null));
            return true;
        }

        if (jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.parseClaims(token);
            String[] subjects = claims.getSubject().split(",");
            String email = subjects[0];
            Long accountId = Long.valueOf(subjects[1]);
            Role role = fromString(claims.get("auth", String.class).substring(5));

            request.setAttribute(PAYLOAD_ATTRIBUTE, new PayloadDto(accountId, email, role));
        }

        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken == null) {
            return null;
        }

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        throw new AuthException(AuthExceptionType.UNSUPPORTED_TOKEN);
    }

    private Role fromString(String roleString) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(roleString)) {
                return role;
            }
        }
        throw new RuntimeException("Invalid role : " + roleString);
    }
}
