package ddog.persistence.rdb.adapter;

import ddog.domain.groomer.Groomer;
import ddog.domain.groomer.dto.SearchGroomerResultDto;
import ddog.domain.groomer.enums.GroomingBadge;
import ddog.persistence.rdb.jpa.entity.GroomerJpaEntity;
import ddog.persistence.rdb.jpa.repository.GroomerJpaRepository;
import ddog.domain.groomer.port.GroomerPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class GroomerRepository implements GroomerPersist {

    private final GroomerJpaRepository groomerJpaRepository;

    @Override
    public Optional<Groomer> findByAccountId(Long accountId) {
        return groomerJpaRepository.findByAccountId(accountId)
                .map(GroomerJpaEntity::toModel);
    }

    @Override
    public Groomer save(Groomer newGroomer) {
        return groomerJpaRepository.save(GroomerJpaEntity.from(newGroomer)).toModel();
    }

    @Override
    public Optional<Groomer> findByGroomerId(Long groomerId) {
        return groomerJpaRepository.findByGroomerId(groomerId)
                .map(GroomerJpaEntity::toModel);
    }

    @Override
    public List<SearchGroomerResultDto> findGroomersByKeywords(String address, String name, GroomingBadge badge, Pageable pageable) {
        List<Long> groomerIds = groomerJpaRepository.findPagedGroomerIds(address, name, badge, pageable).getContent();
        List<Object[]> groomersWithDetails = groomerJpaRepository.findGroomersWithDetails(groomerIds);

        Map<Long, SearchGroomerResultDto> dtoMap = new HashMap<>();

        for (Object[] groomerWithDetail : groomersWithDetails) {
            Long accountId = ((Number) groomerWithDetail[0]).longValue();
            String groomerName = (String) groomerWithDetail[1];
            String imageUrl = (String) groomerWithDetail[2];

            if (!dtoMap.containsKey(accountId)){
                dtoMap.put(accountId, new SearchGroomerResultDto(accountId, groomerName, imageUrl, new ArrayList<>()));
            }

            if (groomerWithDetail[3] == null) {
                continue;
            }

            GroomingBadge groomingBadge = GroomingBadge.valueOf((String) groomerWithDetail[3]);
            List<GroomingBadge> groomingBadges = dtoMap.get(accountId).getBadges();
            groomingBadges.add(groomingBadge);
            dtoMap.put(accountId, new SearchGroomerResultDto(accountId, groomerName, imageUrl, groomingBadges));
        }

        return new ArrayList<>(dtoMap.values());
    }

    @Override
    public void deleteByAccountId(Long accountId) {
        groomerJpaRepository.deleteByAccountId(accountId);
    }
}
