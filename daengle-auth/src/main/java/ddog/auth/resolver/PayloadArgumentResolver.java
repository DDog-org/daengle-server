package ddog.auth.resolver;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.AuthException;
import ddog.auth.exception.AuthExceptionType;
import ddog.auth.interceptor.JwtAuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayloadArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(PayloadDto.class)
                && parameter.hasParameterAnnotation(AuthPayload.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        PayloadDto payload = (PayloadDto) request.getAttribute(JwtAuthInterceptor.PAYLOAD_ATTRIBUTE);

        AuthPayload authPayload = parameter.getParameterAnnotation(AuthPayload.class);
        if (authPayload.required() && (payload == null || payload.getAccountId() == null)) {
            throw new AuthException(AuthExceptionType.INVALID_TOKEN);
        }

        return payload;
    }
}