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

    @Query("SELECT v FROM Groomers v " +
            "LEFT JOIN v.keywords k " +
            "WHERE (:address IS NULL OR :address = '' OR v.address LIKE CONCAT('%', :address, '%')) " +
            "AND (:name IS NULL OR :name = '' OR v.name LIKE CONCAT('%', :name, '%')) " +
            "AND (:badge IS NULL OR :badge = '' OR :badge MEMBER OF v.badges)")
    Page<GroomerJpaEntity> findGroomersByKeywords(
            @Param("address") String address,
            @Param("name") String name,
            @Param("badge") GroomingBadge badge,
            Pageable pageable
    );
    void deleteByAccountId(Long accountId);
}
