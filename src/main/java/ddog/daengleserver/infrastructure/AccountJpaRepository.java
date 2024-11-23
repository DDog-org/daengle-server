package ddog.daengleserver.infrastructure;

import ddog.daengleserver.domain.Provider;
import ddog.daengleserver.domain.Role;
import ddog.daengleserver.infrastructure.jpa.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long> {

    boolean existsByEmailAndRole(String email, Role role);

    Optional<AccountJpaEntity> findByEmailAndProvider(String email, Provider provider);

    Optional<AccountJpaEntity> findByEmailAndRole(String email, Role role);
}
