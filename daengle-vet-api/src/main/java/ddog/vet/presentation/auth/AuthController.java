package ddog.vet.presentation.auth;

import ddog.auth.dto.AccessTokenInfo;
import ddog.auth.dto.KakaoAccessTokenDto;
import ddog.auth.dto.RefreshTokenDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.vet.application.auth.AuthService;
import ddog.vet.presentation.auth.dto.LoginResult;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ddog.auth.exception.common.CommonResponseEntity.success;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vet")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public CommonResponseEntity<LoginResult> kakaoLogin(@RequestBody KakaoAccessTokenDto kakaoAccessTokenDto, HttpServletResponse response) {
        return success(authService.kakaoOAuthLogin(kakaoAccessTokenDto.getKakaoAccessToken(), response));
    }

    @PostMapping("/refresh-token")
    public CommonResponseEntity<AccessTokenInfo> reGenerateAccessToken(@RequestBody RefreshTokenDto refreshTokenDto, HttpServletResponse response) {
        return success(authService.reGenerateAccessToken(refreshTokenDto.getRefreshToken(), response));
    }
}
