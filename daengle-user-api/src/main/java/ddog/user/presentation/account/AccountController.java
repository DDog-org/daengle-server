package ddog.user.presentation.account;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.user.application.AccountService;
import ddog.user.presentation.account.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/available-nickname")
    public CommonResponseEntity<ValidResponse.Nickname> hasNickname(@RequestBody CheckNickname request) {
        return success(accountService.hasNickname(request.getNickname()));
    }

    @GetMapping("/breed/list")
    public CommonResponseEntity<BreedList> getBreedList() {
        return success(accountService.getBreedInfos());
    }

    @PostMapping("/join-with-pet")
    public CommonResponseEntity<SignUpResp> joinUserWithPet(@RequestBody SignUpWithPet request, HttpServletResponse response) {
        return success(accountService.signUpWithPet(request, response));
    }

    @PostMapping("/join-without-pet")
    public CommonResponseEntity<SignUpResp> joinUserWithoutPet(@RequestBody SignUpWithoutPet request, HttpServletResponse response) {
        return success(accountService.signUpWithoutPet(request, response));
    }

    @GetMapping("/info")
    public CommonResponseEntity<ProfileInfo.UpdatePage> getUserProfileInfo(@AuthPayload PayloadDto payloadDto) {
        return success(accountService.getUserProfileInfo(payloadDto.getAccountId()));
    }

    @PatchMapping("/info")
    public CommonResponseEntity<AccountResp> updateUserInfo(@RequestBody UpdateUserInfoReq request, PayloadDto payloadDto) {
        return success(accountService.updateUserInfo(request, payloadDto.getAccountId()));
    }

    @PostMapping("/pet")
    public CommonResponseEntity<AccountResp> addPet(@RequestBody AddPetInfo request, PayloadDto payloadDto) {
        return success(accountService.addPet(request, payloadDto.getAccountId()));
    }

    @GetMapping("/pet-info")
    public CommonResponseEntity<PetInfo> getPetInfo(@AuthPayload PayloadDto payloadDto) {
        return success(accountService.getPetInfo(payloadDto.getAccountId()));
    }

    @PatchMapping("/pet-info")
    public CommonResponseEntity<AccountResp> updatePetInfo(@RequestBody UpdatePetInfo request, PayloadDto payloadDto) {
        return success(accountService.updatePetInfo(request, payloadDto.getAccountId()));
    }

    @DeleteMapping("/pet")
    public CommonResponseEntity<AccountResp> deletePet(@RequestBody DeletePetId request) {
        return success(accountService.deletePet(request.getPetId()));
    }

    @GetMapping("/withdraw-info")
    public CommonResponseEntity<WithdrawInfoResp> getWithdrawInfo(@AuthPayload PayloadDto payloadDto) {
        return success(accountService.getWithdrawInfo(payloadDto.getAccountId()));
    }

    @DeleteMapping("/info")
    public CommonResponseEntity<WithdrawResp> withdraw(@AuthPayload PayloadDto payloadDto) {
        return success(accountService.withdraw(payloadDto.getAccountId()));
    }
}
