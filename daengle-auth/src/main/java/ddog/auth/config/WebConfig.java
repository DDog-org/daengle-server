package ddog.auth.config;

import ddog.auth.interceptor.JwtAuthInterceptor;
import ddog.auth.resolver.PayloadArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final PayloadArgumentResolver payloadArgumentResolver;
    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(payloadArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .excludePathPatterns(
                        "/api/*/test/**",
                        "/api/*/kakao",
                        "/api/*/refresh-token",
                        "/api/user/available-nickname",
                        "/api/user/breed/list",
                        "/api/user/join-with-pet",
                        "/api/user/join-without-pet",
                        "/api/groomer/join",
                        "/api/vet/join"
                );
    }
}