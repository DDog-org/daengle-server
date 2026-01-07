package ddog.chat.application;

import ddog.chat.application.adapter.out.ChatMessageSender;
import ddog.chat.application.dto.response.ProfileInfo;
import ddog.chat.presentation.dto.ChatMessageReq;
import ddog.chat.presentation.dto.ChatMessagesListResp;
import ddog.chat.presentation.dto.PartnerChatRoomListResp;
import ddog.chat.presentation.dto.UserChatRoomListResp;
import ddog.domain.account.Account;
import ddog.domain.account.Role;
import ddog.domain.account.port.AccountPersist;
import ddog.domain.chat.ChatMessage;
import ddog.domain.chat.ChatRoom;
import ddog.domain.chat.dto.ChatRoomListDto;
import ddog.domain.chat.enums.PartnerType;
import ddog.domain.chat.port.ChatMessagePersist;
import ddog.domain.chat.port.ChatRoomPersist;
import ddog.domain.groomer.port.GroomerPersist;
import ddog.domain.user.User;
import ddog.domain.user.port.UserPersist;
import ddog.domain.vet.port.VetPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessagePersist chatMessagePersist;
    private final ChatRoomPersist chatRoomPersist;
    private final UserPersist userPersist;
    private final GroomerPersist groomerPersist;
    private final VetPersist vetPersist;
    private final AccountPersist accountPersist;
    private final ChatMessageSender messageConnector;

    private ChatRoom startChat(Role role, Long accountId, Long otherUserId) {
        return findOrSaveChatRoom(role, accountId, otherUserId);
    }

    @Transactional(readOnly = true)
    public ChatMessagesListResp getAllMessagesByRoomId(Role role, Long userAccountId, Long otherUserId) {
        ChatRoom savedChatRoom = startChat(role, userAccountId, otherUserId);

        Account savedOtherUser = accountPersist.findById(otherUserId);

        ProfileInfo profileInfo = getProfileInfo(savedOtherUser);

        List<ChatMessage> savedMessages = chatMessagePersist.findByChatRoomId(savedChatRoom.getChatRoomId());

        List<Map<String, Object>> messagesByDate = savedMessages.stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                .collect(Collectors.groupingBy(
                        message -> message.getTimestamp().toLocalDate(),
                        LinkedHashMap::new,
                        Collectors.mapping(message -> Map.of(
                                "messageId", message.getMessageId(),
                                "messageSenderId", message.getSenderId(),
                                "messageContent", message.getContent(),
                                "messageTime", message.getTimestamp(),
                                "messageType", message.getMessageType()
                        ), Collectors.toList())
                ))
                .entrySet()
                .stream()
                .map(entry -> Map.of(
                        "date", entry.getKey().toString(),
                        "messages", entry.getValue()
                ))
                .collect(Collectors.toList());

        return ChatMessagesListResp.builder()
                .roomId(savedChatRoom.getChatRoomId())
                .userId(userAccountId)
                .otherId(otherUserId)
                .otherName(profileInfo.name())
                .otherProfile(profileInfo.profileImageUrl())
                .messagesGroupedByDate(messagesByDate)
                .build();
    }

    private ProfileInfo getProfileInfo(Account account) {
        if (account == null) return new ProfileInfo(null, null);

        return switch (account.getRole()) {
            case GROOMER -> groomerPersist.findByAccountId(account.getAccountId())
                    .map(groomer -> new ProfileInfo(groomer.getName(), groomer.getImageUrl()))
                    .orElse(new ProfileInfo(null, null));
            case VET -> vetPersist.findByAccountId(account.getAccountId())
                    .map(vet -> new ProfileInfo(vet.getName(), vet.getImageUrl()))
                    .orElse(new ProfileInfo(null, null));
            case DAENGLE -> userPersist.findByAccountId(account.getAccountId())
                    .map(user -> new ProfileInfo(user.getNickname(), user.getImageUrl()))
                    .orElse(new ProfileInfo(null, null));
            default -> new ProfileInfo(null, null);
        };
    }

    public UserChatRoomListResp findUserChatRoomList(Long userId, PartnerType partnerType) {
        List<ChatRoomListDto> savedChatRooms = chatRoomPersist.findByUserIdAndPartnerType(userId, partnerType);

        if (savedChatRooms == null || savedChatRooms.isEmpty()) {
            return UserChatRoomListResp.builder().roomList(Collections.emptyList()).build();
        }

        List<UserChatRoomListResp.RoomList> userChatRoomListResps = new ArrayList<>();

        for (ChatRoomListDto savedChatRoom : savedChatRooms) {
            String partnerName = savedChatRoom.getPartnerName();
            String partnerProfile = savedChatRoom.getPartnerProfile();

            ChatMessage savedLastMessages = chatMessagePersist.findLatestMessageByRoomId(savedChatRoom.getRoomId());
            String lastMessage = (savedLastMessages != null) ? savedLastMessages.getContent() : "";
            String messageTime = (savedLastMessages != null)
                    ? savedLastMessages.getTimestamp().toString()
                    : "";

            userChatRoomListResps.add(UserChatRoomListResp.RoomList.builder()
                    .roomId(savedChatRoom.getRoomId())
                    .otherId(savedChatRoom.getPartnerId())
                    .otherName(partnerName)
                    .otherProfile(partnerProfile)
                    .messageTime(messageTime)
                    .lastMessage(lastMessage)
                    .partnerType(savedChatRoom.getPartnerType())
                    .build());
        }

        return UserChatRoomListResp.builder()
                .roomList(userChatRoomListResps)
                .build();
    }

    @Transactional
    public boolean deleteChatRoom(Long roomId) {
        ChatRoom savedChatRoom = chatRoomPersist.findByRoomId(roomId);
        if (savedChatRoom == null) {
            return false;
        }
        chatRoomPersist.exitChatRoom(savedChatRoom.getUserId(), savedChatRoom.getPartnerId());

        return true;
    }

    @Transactional
    public ChatMessage sendAndSaveMessage(ChatMessageReq chatMessageReq, Long roomId, Long accountId) {
        Long recipientId = findMessageRecipientByRoomId(roomId, chatMessageReq.getSenderId());
        Long messageId = System.currentTimeMillis();

        ChatMessage chatMessage = ChatMessage.builder()
                .messageId(messageId)
                .chatRoomId(roomId)
                .messageType(chatMessageReq.getMessageType())
                .senderId(accountId)
                .content(chatMessageReq.getMessageContent())
                .recipientId(recipientId)
                .timestamp(LocalDateTime.now())
                .build();

        messageConnector.sendMessage(roomId, chatMessage);

        return chatMessagePersist.save(chatMessage);
    }

    public PartnerChatRoomListResp findPartnerChatRoomList(Long userId) {
        List<ChatRoom> savedChatRooms = chatRoomPersist.findByPartnerId(userId);

        if (savedChatRooms == null || savedChatRooms.isEmpty()) {
            return PartnerChatRoomListResp.builder().roomList(Collections.emptyList()).build();
        }

        List<PartnerChatRoomListResp.RoomList> partnerChatRoomListResps = new ArrayList<>();
        for (ChatRoom savedChatRoom : savedChatRooms) {

            User savedUser = userPersist.findByAccountId(savedChatRoom.getUserId()).orElse(null);

            ChatMessage savedLastMessages = chatMessagePersist.findLatestMessageByRoomId(savedChatRoom.getChatRoomId());

            partnerChatRoomListResps.add(PartnerChatRoomListResp.RoomList.builder()
                    .roomId(savedChatRoom.getChatRoomId())
                    .otherId(savedChatRoom.getUserId())
                    .otherName((savedUser != null) ? savedUser.getNickname() : null)
                    .otherProfile((savedUser != null) ? savedUser.getImageUrl() : null)
                    .messageTime((savedLastMessages != null) ? savedLastMessages.getTimestamp().toString() : null)
                    .lastMessage((savedLastMessages != null) ? savedLastMessages.getContent() : null)
                    .build());

        }
        return PartnerChatRoomListResp.builder()
                .roomList(partnerChatRoomListResps)
                .build();
    }

    @Transactional
    public ChatRoom findOrSaveChatRoom(Role role, Long accountId, Long otherUserId) {
        ChatRoom toSaveChat = null;
        if (role.equals(Role.DAENGLE)) {
            if (accountPersist.findById(otherUserId).getRole().equals(Role.VET)) {
                toSaveChat = chatRoomPersist.enterChatRoom(accountId, otherUserId, PartnerType.VET_PARTNER);
            } else if (accountPersist.findById(otherUserId).getRole().equals(Role.GROOMER)) {
                toSaveChat = chatRoomPersist.enterChatRoom(accountId, otherUserId, PartnerType.GROOMER_PARTNER);
            }
        } else {
            if (role.equals(Role.GROOMER)) {
                toSaveChat = chatRoomPersist.enterChatRoom(otherUserId, accountId, PartnerType.GROOMER_PARTNER);
            } else if (role.equals(Role.VET)) {
                toSaveChat = chatRoomPersist.enterChatRoom(otherUserId, accountId, PartnerType.VET_PARTNER);
            }
        }
        return toSaveChat;
    }

    private Long findMessageRecipientByRoomId(Long roomId, Long senderId) {
        ChatRoom savedChatRoom = chatRoomPersist.findByRoomId(roomId);
        if (savedChatRoom == null) {
            return null;
        }
        if (savedChatRoom.getUserId().equals(senderId)) return savedChatRoom.getPartnerId();
        else return savedChatRoom.getUserId();
    }
}
