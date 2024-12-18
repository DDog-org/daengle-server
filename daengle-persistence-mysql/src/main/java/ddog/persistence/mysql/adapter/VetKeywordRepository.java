package ddog.persistence.mysql.adapter;

import ddog.domain.vet.VetKeyword;
import ddog.domain.vet.port.VetKeywordPersist;
import ddog.persistence.mysql.jpa.entity.VetKeywordJpaEntity;
import ddog.persistence.mysql.jpa.repository.VetKeywordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VetKeywordRepository implements VetKeywordPersist {

    private final VetKeywordJpaRepository vetKeywordJpaRepository;

    @Override
    public VetKeyword save(VetKeyword vetKeyword) {
        return vetKeywordJpaRepository.save(VetKeywordJpaEntity.from(vetKeyword))
                .toModel();
    }
}