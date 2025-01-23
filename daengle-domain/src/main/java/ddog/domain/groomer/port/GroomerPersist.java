package ddog.domain.groomer.port;

import ddog.domain.groomer.Groomer;
import ddog.domain.groomer.enums.GroomingBadge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GroomerPersist {

    Optional<Groomer> findByAccountId(Long accountId);

    Groomer save(Groomer newGroomer);

    Optional<Groomer> findByGroomerId(Long groomerId);

    List<Groomer> findGroomersByKeywords(String address, String name, GroomingBadge badge, Pageable pageable);

    void deleteByAccountId(Long accountId);
}
