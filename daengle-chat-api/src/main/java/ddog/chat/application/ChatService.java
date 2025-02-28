package ddog.chat.application;

import ddog.chat.application.adapter.MessageConnector;
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
import ddog.domain.groomer.Groomer;
import ddog.domain.groomer.port.GroomerPersist;
import ddog.domain.user.User;
import ddog.domain.user.port.UserPersist;
import ddog.domain.vet.Vet;
import ddog.domain.vet.port.VetPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final MessageConnector messageConnector;

    private ChatRoom startChat(Role role, Long accountId, Long otherUserId) {
        return findOrSaveChatRoom(role, accountId, otherUserId);
    }

    public ChatMessagesListResp getAllMessagesByRoomId(Role role, Long userAccountId, Long otherUserId) {
        ChatRoom savedChatRoom = startChat(role, userAccountId, otherUserId);
        String otherUserProfile = null;
        String otherUserName = null;
        if (role.equals(Role.DAENGLE)) {
            Account savedOtherUser = accountPersist.findById(otherUserId);
            if (savedOtherUser.getRole().equals(Role.GROOMER)) {
                Groomer savedGroomer = groomerPersist.findByAccountId(otherUserId).orElse(null);
                otherUserProfile = (savedGroomer != null) ? savedGroomer.getImageUrl() : null;
                otherUserName = (savedGroomer != null) ? savedGroomer.getName() : null;
            } else if (savedOtherUser.getRole().equals(Role.VET)) {
                Vet savedVet = vetPersist.findByAccountId(otherUserId).orElse(null);
                otherUserProfile = (savedVet != null) ? savedVet.getImageUrl() : null;
                otherUserName = (savedVet != null) ? savedVet.getName() : null;
            }
        } else {
            User savedUser = userPersist.findByAccountId(otherUserId).orElse(null);
            otherUserProfile = (savedUser != null) ? savedUser.getImageUrl() : null;
            otherUserName = (savedUser != null) ? savedUser.getNickname() : null;
        }

        List<ChatMessage> savedMessages = chatMessagePersist.findByChatRoomId(savedChatRoom.getChatRoomId());

        if (savedMessages == null || savedMessages.isEmpty()) {
            return ChatMessagesListResp.builder()
                    .roomId(savedChatRoom.getChatRoomId())
                    .userId(userAccountId)
                    .otherId(otherUserId)
                    .otherName(otherUserName)
                    .otherProfile(otherUserProfile)
                    .messagesGroupedByDate(Collections.emptyList())
                    .build();
        }

        Map<LocalDate, List<ChatMessagesListResp.ChatMessageSummary>> groupedMessages = savedMessages.stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                .collect(Collectors.groupingBy(
                        message -> message.getTimestamp().toLocalDate(),
                        Collectors.mapping(
                                message -> ChatMessagesListResp.ChatMessageSummary.builder()
                                        .messageId(message.getMessageId())
                                        .messageSenderId(message.getSenderId())
                                        .messageContent(message.getContent())
                                        .messageTime(message.getTimestamp())
                                        .messageType(message.getMessageType())
                                        .build(),
                                Collectors.toList()
                        )));

        List<Map<String, Object>> messagesByDate = groupedMessages.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> dateMap = new HashMap<>();
                    dateMap.put("date", entry.getKey().toString());
                    dateMap.put("messages", entry.getValue());
                    return dateMap;
                })
                .collect(Collectors.toList());

        return ChatMessagesListResp.builder()
                .roomId(savedChatRoom.getChatRoomId())
                .userId(userAccountId)
                .otherId(otherUserId)
                .otherName(otherUserName)
                .otherProfile(otherUserProfile)
                .messagesGroupedByDate(messagesByDate)
                .build();
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

    public boolean deleteChatRoom(Long roomId) {
        ChatRoom savedChatRoom = chatRoomPersist.findByRoomId(roomId);
        if (savedChatRoom == null) {
            return false;
        }
        chatRoomPersist.exitChatRoom(savedChatRoom.getUserId(), savedChatRoom.getPartnerId());

        return true;
    }

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
