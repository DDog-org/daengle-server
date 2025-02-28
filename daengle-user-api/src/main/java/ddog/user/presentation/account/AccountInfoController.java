package ddog.user.presentation.account;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.user.application.AccountInfoService;
import ddog.user.presentation.account.dto.MyProfileResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class AccountInfoController {

    private final AccountInfoService accountInfoService;

    @GetMapping("/mypage")
    public CommonResponseEntity<MyProfileResp> getMyProfileInfo(@AuthPayload PayloadDto payloadDto) {
        return success(accountInfoService.findAccountInfo(payloadDto.getAccountId()));
    }
}
