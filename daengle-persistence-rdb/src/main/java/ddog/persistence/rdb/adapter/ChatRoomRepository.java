package ddog.persistence.rdb.adapter;

import ddog.domain.chat.ChatRoom;
import ddog.domain.chat.dto.ChatRoomListDto;
import ddog.domain.chat.enums.PartnerType;
import ddog.persistence.rdb.jpa.entity.ChatRoomJpaEntity;
import ddog.persistence.rdb.jpa.repository.ChatRoomJpaRepository;
import ddog.domain.chat.port.ChatRoomPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository implements ChatRoomPersist {

    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @Override
    public ChatRoom enterChatRoom(Long userId, Long partnerId, PartnerType partnerType) {

        if (chatRoomJpaRepository.findByUserIdAndPartnerId(userId, partnerId) == null) {
            ChatRoom chatRoom = ChatRoom.builder()
                    .userId(userId)
                    .partnerId(partnerId)
                    .partnerType(partnerType)
                    .build();
            return chatRoomJpaRepository.save(ChatRoomJpaEntity.from(chatRoom)).toModel();
        }
        else {
            return chatRoomJpaRepository.findByUserIdAndPartnerId(userId, partnerId).toModel();
        }
    }

    @Override
    public void exitChatRoom(Long userId, Long partnerId) {
        ChatRoom findRoom = chatRoomJpaRepository.findByUserIdAndPartnerId(userId, partnerId).toModel();
        chatRoomJpaRepository.deleteById(findRoom.getChatRoomId());
    }

    @Override
    public ChatRoom findByUserIdPartnerId(Long userId, Long partnerId) {
        return chatRoomJpaRepository.findByUserIdAndPartnerId(userId, partnerId).toModel();
    }

    @Override
    public ChatRoom findByRoomId(Long roomId) {
        return chatRoomJpaRepository.findByChatRoomId(roomId).toModel();
    }

    @Override
    public List<ChatRoom> findByUserId(Long userId) {
        return chatRoomJpaRepository.findByUserId(userId).stream().map(ChatRoomJpaEntity::toModel).toList();
    }

    @Override
    public List<ChatRoom> findByPartnerId(Long partnerId) {
        return chatRoomJpaRepository.findByPartnerId(partnerId).stream().map(ChatRoomJpaEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomListDto> findByUserIdAndPartnerType(Long userId, PartnerType partnerType) {
        List<Object[]> chatRoomsWithInfo = chatRoomJpaRepository.findAllByUserIdAndPartnerType(userId, partnerType);

        Map<Long, ChatRoomListDto> result = new HashMap<>();

        for (Object[] chatRoomInfo : chatRoomsWithInfo) {
            Long roomId = ((Number) chatRoomInfo[0]).longValue();
            Long partnerId = ((Number) chatRoomInfo[1]).longValue();
            String partnerName = (String) chatRoomInfo[2];
            String partnerProfile = (String) chatRoomInfo[3];
            PartnerType retrievedPartnerType = (PartnerType) chatRoomInfo[4];

            result.putIfAbsent(partnerId, new ChatRoomListDto(roomId, partnerId, partnerName, partnerProfile, retrievedPartnerType));
        }
        return new ArrayList<>(result.values());
    }
}
