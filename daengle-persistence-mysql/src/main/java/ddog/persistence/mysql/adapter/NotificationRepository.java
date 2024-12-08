package ddog.persistence.mysql.adapter;

import ddog.domain.notification.Notification;
import ddog.persistence.mysql.jpa.entity.NotificationJpaEntity;
import ddog.persistence.mysql.jpa.repository.NotificationJpaRepository;
import ddog.persistence.mysql.port.NotificationPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepository implements NotificationPersist {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public List<Notification> findNotificationsByUserId(Long userId) {
        return Optional.ofNullable(notificationJpaRepository.findByUserId(userId))
                .filter(list -> list.isEmpty())
                .orElseThrow(() -> new RuntimeException("No Notification Message"))
                .stream()
                .map(NotificationJpaEntity::toModel)
                .toList();
    }

    @Override
    public void saveNotificationWithLogoutUser(Notification notification) {
        notificationJpaRepository.save(NotificationJpaEntity.from(notification));
    }

    @Override
    public Notification findNotificationById(Long notificationId) {
        return notificationJpaRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("No Notification Id"))
                .toModel();
    }

    @Override
    public void deleteNotificationById(Long notificationId) {
        notificationJpaRepository.deleteById(notificationId);
    }
}
