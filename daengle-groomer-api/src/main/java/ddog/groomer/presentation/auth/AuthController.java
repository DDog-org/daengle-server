package ddog.groomer.presentation.auth;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.AccessTokenInfo;
import ddog.auth.dto.KakaoAccessTokenDto;
import ddog.auth.dto.PayloadDto;
import ddog.auth.dto.RefreshTokenDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.groomer.application.auth.AuthService;
import ddog.groomer.presentation.auth.dto.LoginResult;
import ddog.groomer.presentation.auth.dto.ValidateResp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groomer")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public CommonResponseEntity<LoginResult> kakaoLogin(@RequestBody KakaoAccessTokenDto kakaoAccessTokenDto, HttpServletResponse response) {
        return success(authService.kakaoOAuthLogin(kakaoAccessTokenDto.getKakaoAccessToken(), response));
    }

    @PostMapping("/refresh-token")
    public CommonResponseEntity<AccessTokenInfo> reGenerateAccessToken(HttpServletRequest request, HttpServletResponse response) {
        return success(authService.reGenerateAccessToken(request, response));
    }

    @GetMapping("/validate")
    public CommonResponseEntity<ValidateResp> validateMember(@AuthPayload(required = false) PayloadDto payloadDto) {
        return success(authService.validateMember(payloadDto.getAccountId()));
    }
}
