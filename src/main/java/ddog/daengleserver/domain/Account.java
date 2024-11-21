package ddog.daengleserver.domain;

import ddog.daengleserver.global.auth.config.enums.Provider;
import ddog.daengleserver.global.auth.config.enums.Role;
import ddog.daengleserver.presentation.dto.response.AccountInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class Account {

    private final Long id;
    private final String email;
    private final String nickname;
    private final Provider provider;
    private final Role role;

    public Account withNickname(String nickname) {
        return Account.builder()
                .id(this.id)
                .email(this.email)
                .nickname(nickname)
                .provider(this.provider)
                .role(this.role)
                .build();
    }

    public AccountInfoDto withAccount() {
        return AccountInfoDto.builder()
                .email(this.email)
                .nickname(this.nickname)
                .provider(this.provider)
                .build();
    }
}
