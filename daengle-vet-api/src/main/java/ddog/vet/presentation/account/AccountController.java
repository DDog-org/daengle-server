package ddog.vet.presentation.account;

import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.vet.application.AccountService;
import ddog.vet.presentation.account.dto.ModifyInfoReq;
import ddog.vet.presentation.account.dto.ProfileInfo;
import ddog.vet.presentation.account.dto.SignUpReq;
import ddog.vet.presentation.account.dto.SignUpResp;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;


@RestController
@RequestMapping("/api/vet")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/join")
    public CommonResponseEntity<SignUpResp> signUp(@RequestBody SignUpReq request, HttpServletResponse response) {
        return success(accountService.signUp(request, response));
    }

    @GetMapping("/modify-page")
    public CommonResponseEntity<ProfileInfo.ModifyPage> getModifyInfo(PayloadDto payloadDto) {
        return success(accountService.getModifyPage(payloadDto.getAccountId()));
    }

    @PatchMapping("/info")
    public CommonResponseEntity<String> modifyInfo(@RequestBody ModifyInfoReq request, PayloadDto payloadDto) {
        accountService.modifyInfo(request, payloadDto.getAccountId());
        return success("병원 페이지가 정상적으로 수정됐습니다.");
    }
}
