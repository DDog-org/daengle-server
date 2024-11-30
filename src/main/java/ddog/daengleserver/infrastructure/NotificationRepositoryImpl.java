package ddog.daengleserver.infrastructure;

import ddog.daengleserver.application.repository.NotificationRepository;
import ddog.daengleserver.domain.Notification;
import ddog.daengleserver.infrastructure.po.NotificationJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;
    @Override
    public List<Notification> findByUserId(Long userId) {
        return Optional.ofNullable(notificationJpaRepository.findByUserId(userId))
                .filter(list -> list.isEmpty())
                .orElseThrow(() -> new RuntimeException("No Notification Message"))
                .stream()
                .map(NotificationJpaEntity::toModel)
                .toList();
    }

    @Override
    public void save(Notification notification) {
        notificationJpaRepository.save(NotificationJpaEntity.from(notification));
    }

    @Override
    public Notification findById(Long notificationId) {
        return notificationJpaRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("No Notification Id"))
                .toModel();
    }

    @Override
    public void delete(Long notificationId) {
        notificationJpaRepository.deleteById(notificationId);
    }
}
