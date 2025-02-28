package ddog.chat.presentation;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.chat.application.ChatService;
import ddog.chat.presentation.dto.ChatMessageReq;
import ddog.chat.presentation.dto.ChatMessagesListResp;
import ddog.chat.presentation.dto.PartnerChatRoomListResp;
import ddog.chat.presentation.dto.UserChatRoomListResp;
import ddog.domain.chat.ChatMessage;
import ddog.domain.chat.ChatRoom;
import ddog.domain.chat.enums.PartnerType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/start")
    public CommonResponseEntity<ChatRoom> findChatRoom(@AuthPayload PayloadDto payloadDto,
                                                       @RequestParam Long otherId) {
        return success(chatService.findOrSaveChatRoom(payloadDto.getRole(), payloadDto.getAccountId(), otherId));
    }

    @GetMapping("/with")
    public CommonResponseEntity<ChatMessagesListResp> startChatMessage(@AuthPayload PayloadDto payloadDto,
                                                                       @RequestParam Long otherId) {
        return success(chatService.getAllMessagesByRoomId(payloadDto.getRole(), payloadDto.getAccountId(), otherId));
    }

    @DeleteMapping("/delete/{roomId}")
    public CommonResponseEntity<Boolean> deleteChatRoom(@PathVariable Long roomId) {
        return success(chatService.deleteChatRoom(roomId));
    }

    @PostMapping("/messages/{roomId}")
    public CommonResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessageReq messageReq,
                                                         @PathVariable Long roomId,
                                                         @AuthPayload PayloadDto payloadDto) {
        ChatMessage savedMessage = chatService.sendAndSaveMessage(messageReq, roomId, payloadDto.getAccountId());
        messagingTemplate.convertAndSend("/sub/" + roomId, savedMessage);
        return success(savedMessage);
    }

    @GetMapping("/user/groomer/list")
    public CommonResponseEntity<UserChatRoomListResp> findGroomerUserChatRoomList(@AuthPayload PayloadDto payloadDto){
        return success(chatService.findUserChatRoomList(payloadDto.getAccountId(), PartnerType.GROOMER_PARTNER));
    }
    @GetMapping("/user/vet/list")
    public CommonResponseEntity<UserChatRoomListResp> findVetUserChatRoomList(@AuthPayload PayloadDto payloadDto){
        return success(chatService.findUserChatRoomList(payloadDto.getAccountId(), PartnerType.VET_PARTNER));
    }

    @GetMapping("/groomer/list")
    public CommonResponseEntity<PartnerChatRoomListResp> findGroomerChatRoomList(@AuthPayload PayloadDto payloadDto){
        return success(chatService.findPartnerChatRoomList(payloadDto.getAccountId()));
    }

    @GetMapping("/vet/list")
    public CommonResponseEntity<PartnerChatRoomListResp> findVetChatRoomList(@AuthPayload PayloadDto payloadDto){
        return success(chatService.findPartnerChatRoomList(payloadDto.getAccountId()));
    }
}
