package ddog.persistence.dynamodb.adapter;

import ddog.domain.chat.ChatMessage;

import ddog.domain.chat.port.ChatMessagePersist;
import ddog.persistence.dynamodb.entity.ChatMessageDynamoEntity;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ChatMessageDynamoRepository implements ChatMessagePersist {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    private static final String TABLE_NAME = "chat_message";

    private DynamoDbTable<ChatMessageDynamoEntity> getTable() {
        return dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(ChatMessageDynamoEntity.class));
    }

    @Override
    public ChatMessage save(ChatMessage message) {
        ChatMessageDynamoEntity entity = ChatMessageDynamoEntity.from(message);

        try {
            getTable().putItem(entity);
            System.out.println("SAVE SUCCESS");

        } catch (DynamoDbException e) {
            System.err.println("SAVE FAILED" + e.getMessage());
            throw e;
        }

        return message;
    }

    @Override
    public List<ChatMessage> findByChatRoomId(Long chatRoomId) {
        try {
            return getTable()
                    .scan()
                    .items()
                    .stream()
                    .filter(item -> chatRoomId.equals(item.getChatRoomId()))
                    .map(ChatMessageDynamoEntity::toModel)
                    .collect(Collectors.toList());
        } catch (DynamoDbException e) {
            System.err.println("FETCH FAILED" + e.getMessage());
            throw e;
        }
    }
}
