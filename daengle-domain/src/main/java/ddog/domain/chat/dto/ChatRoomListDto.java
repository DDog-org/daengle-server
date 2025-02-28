package ddog.domain.chat.dto;

import ddog.domain.chat.enums.PartnerType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomListDto {
    private Long roomId;
    private Long partnerId;
    private String partnerName;
    private String partnerProfile;
    private PartnerType partnerType;
}
