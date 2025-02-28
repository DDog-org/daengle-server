package ddog.persistence.rdb.jpa.repository;

import ddog.domain.chat.enums.PartnerType;
import ddog.persistence.rdb.jpa.entity.ChatRoomJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoomJpaEntity, Long> {
    List<ChatRoomJpaEntity> findByUserId(Long userId);

    List<ChatRoomJpaEntity> findByPartnerId(Long partnerId);

    ChatRoomJpaEntity findByUserIdAndPartnerId(Long userId, Long partnerId);

    ChatRoomJpaEntity findByChatRoomId(Long chatRoomId);

    @Query("SELECT cr.chatRoomId, a.accountId, " +
            "CASE WHEN a.role = 'GROOMER' THEN g.name ELSE v.name END, " +
            "CASE WHEN a.role = 'GROOMER' THEN g.imageUrl ELSE v.imageUrl END, " +
            "cr.partnerType " +
            "FROM ChatRooms cr " +
            "JOIN Accounts a ON cr.partnerId = a.accountId " +
            "LEFT JOIN Groomers g ON a.role = 'GROOMER' AND a.accountId = g.accountId " +
            "LEFT JOIN Vets v ON a.role = 'VET' AND a.accountId = v.accountId " +
            "WHERE cr.userId = :userId AND cr.partnerType = :partnerType")
    List<Object[]> findAllByUserIdAndPartnerType(Long userId, PartnerType partnerType);
}
