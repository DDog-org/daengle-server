package ddog.domain.chat.port;

import ddog.domain.chat.ChatRoom;
import ddog.domain.chat.dto.ChatRoomListDto;
import ddog.domain.chat.enums.PartnerType;

import java.util.List;

public interface ChatRoomPersist {
    ChatRoom enterChatRoom(Long userId, Long partnerId, PartnerType partnerType);
    void exitChatRoom(Long userId, Long partnerId);
    ChatRoom findByUserIdPartnerId(Long userId, Long partnerId);
    ChatRoom findByRoomId(Long roomId);
    List<ChatRoom> findByUserId(Long userId);
    List<ChatRoom> findByPartnerId(Long partnerId);
    List<ChatRoomListDto> findByUserIdAndPartnerType(Long userId, PartnerType partnerType);
}
