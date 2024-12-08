package ddog.vet.application;

import ddog.domain.review.CareReview;
import ddog.domain.vet.Vet;
import ddog.persistence.mysql.port.CareReviewPersist;
import ddog.persistence.mysql.port.VetPersist;
import ddog.vet.application.exception.account.VetException;
import ddog.vet.application.exception.account.VetExceptionType;
import ddog.vet.presentation.review.dto.ReviewSummaryResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final CareReviewPersist careReviewPersist;
    private final VetPersist vetPersist;

    public List<ReviewSummaryResp> findReviewList(Long accountId, int page, int size) {
        Vet savedVet = vetPersist.findBy(accountId)
                .orElseThrow(() -> new VetException(VetExceptionType.VET_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<CareReview> careReviews = careReviewPersist.findByVetId(savedVet.getVetId(), pageable);

        return careReviews.stream()
                .map(careReview -> ReviewSummaryResp.builder()
                        .careReviewId(careReview.getCareReviewId())
                        .vetId(careReview.getVetId())
                        .careKeywordReviewList(careReview.getCareKeywordReviewList())
                        .revieweeName(careReview.getRevieweeName())
                        .starRating(careReview.getStarRating())
                        .content(careReview.getContent())
                        .build())
                .toList();
    }
}
