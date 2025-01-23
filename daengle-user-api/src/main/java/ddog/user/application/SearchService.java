package ddog.user.application;

import ddog.domain.groomer.Groomer;
import ddog.domain.groomer.enums.GroomingBadge;
import ddog.domain.groomer.port.GroomerPersist;
import ddog.domain.vet.Vet;
import ddog.domain.vet.enums.CareBadge;
import ddog.domain.vet.port.VetPersist;
import ddog.user.presentation.search.dto.GroomerResult;
import ddog.user.presentation.search.dto.SearchVetResultByKeyword;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final GroomerPersist groomerPersist;
    private final VetPersist vetPersist;

    public GroomerResult findGroomerResultsBySearch(int page, int size, String address, String name, GroomingBadge badge) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Groomer> groomerPage = groomerPersist.findGroomersByKeywords(address, name, badge, pageable);

        List<GroomerResult.Results> results = groomerPage.stream()
                .map(groomer -> GroomerResult.Results.builder()
                        .partnerId(groomer.getAccountId())
                        .partnerName(groomer.getName())
                        .partnerImage(groomer.getImageUrl())
                        .groomingBadges(groomer.getBadges())
                        .build())
                .toList();

        return GroomerResult.builder()
                .result(results)
                .page(groomerPage.getNumber())
                .size(groomerPage.getSize())
                .totalElements(groomerPage.getTotalElements())
                .build();

    }
    public SearchVetResultByKeyword getVetResultBySearch(int page, int size, String address, String keyword, CareBadge badge) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vet> vetPage = vetPersist.findVetByKeyword(address, keyword, badge, pageable);

        List<SearchVetResultByKeyword.ResultList> resultList = vetPage.stream()
                .map(vet -> SearchVetResultByKeyword.ResultList.builder()
                        .partnerId(vet.getAccountId())
                        .partnerName(vet.getName())
                        .partnerImage(vet.getImageUrl())
                        .careBadges(vet.getBadges())
                        .build())
                .toList();

        return SearchVetResultByKeyword.builder()
                .result(resultList)
                .page(vetPage.getNumber())
                .size(vetPage.getSize())
                .totalElements(vetPage.getTotalElements())
                .build();
    }
}
