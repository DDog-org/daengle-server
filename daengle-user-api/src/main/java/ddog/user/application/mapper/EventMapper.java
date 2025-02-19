package ddog.user.application.mapper;

import ddog.domain.event.enums.EventType;
import ddog.domain.groomer.Groomer;
import ddog.domain.review.CareReview;
import ddog.domain.review.GroomingReview;
import ddog.domain.review.enums.ReviewStatus;
import ddog.domain.vet.Vet;
import ddog.user.application.dto.event.ReviewEvent;

public class EventMapper {
    public static ReviewEvent createBy(CareReview careReview, Vet vet) {
        return new ReviewEvent(
                careReview.getCareReviewId(),
                careReview.getReviewerId(),
                careReview.getRevieweeName(),
                careReview.getContent(),
                vet.getPhoneNumber(),
                ReviewStatus.REVIEW_SUBMITTED,
                EventType.REVIEW
        );
    }

    public static ReviewEvent createBy(GroomingReview groomingReview, Groomer groomer) {
        return new ReviewEvent(
                groomingReview.getGroomingReviewId(),
                groomingReview.getReviewerId(),
                groomingReview.getRevieweeName(),
                groomingReview.getContent(),
                groomer.getPhoneNumber(),
                ReviewStatus.REVIEW_SUBMITTED,
                EventType.REVIEW
        );
    }
}
