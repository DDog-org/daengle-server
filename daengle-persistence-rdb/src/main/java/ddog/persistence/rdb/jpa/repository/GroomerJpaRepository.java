package ddog.persistence.rdb.jpa.repository;

import ddog.domain.groomer.enums.GroomingBadge;
import ddog.persistence.rdb.jpa.entity.GroomerJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroomerJpaRepository extends JpaRepository<GroomerJpaEntity, Long> {

    Optional<GroomerJpaEntity> findByAccountId(Long accountId);

    Optional<GroomerJpaEntity> findByGroomerId(Long groomerId);

    @Query("SELECT g FROM Groomers g " +
            "WHERE (:address IS NULL OR :address = '' OR g.address LIKE CONCAT('%', :address, '%')) " +
            "AND (:name IS NULL OR :name = '' OR g.name LIKE CONCAT('%', :name, '%')) " +
            "AND (:badge IS NULL OR :badge MEMBER OF g.badges)")
    Page<GroomerJpaEntity> findGroomersByKeywords(
            @Param("address") String address,
            @Param("name") String name,
            @Param("badge") GroomingBadge badge,
            Pageable pageable
    );
    void deleteByAccountId(Long accountId);
}
