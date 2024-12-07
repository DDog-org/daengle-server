package ddog.user.application;

import ddog.domain.payment.Reservation;
import ddog.domain.review.CareReview;
import ddog.domain.review.dto.ModifyCareReviewInfo;
import ddog.domain.review.dto.PostCareReviewInfo;
import ddog.persistence.mysql.port.CareReviewPersist;
import ddog.persistence.mysql.port.ReservationPersist;
import ddog.user.application.exception.ReservationException;
import ddog.user.application.exception.ReservationExceptionType;
import ddog.user.application.exception.ReviewException;
import ddog.user.application.exception.ReviewExceptionType;
import ddog.user.presentation.review.dto.ReviewResp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CareReviewService {

    private final CareReviewPersist careReviewPersist;
    private final ReservationPersist reservationPersist;

    public ReviewResp postReview(PostCareReviewInfo postCareReviewInfo) {
        Reservation reservation = reservationPersist.findBy(postCareReviewInfo.getReservationId()).orElseThrow(()
                -> new ReservationException(ReservationExceptionType.RESERVATION_NOT_FOUND));

        CareReview careReviewToSave = CareReview.createBy(reservation, postCareReviewInfo);
        CareReview savedCareReview = careReviewPersist.save(careReviewToSave);

        //TODO 댕글미터 계산해서 영속

        return ReviewResp.builder()
                .reviewId(savedCareReview.getCareReviewId())
                .reviewerId(savedCareReview.getReviewerId())
                .revieweeId(savedCareReview.getVetId())
                .build();
    }

    public ReviewResp modifyReview(ModifyCareReviewInfo modifyCareReviewInfo) {
        CareReview savedCareReview = careReviewPersist.findBy(modifyCareReviewInfo.getCareReviewId())
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        CareReview modifiedReview = CareReview.modifyBy(savedCareReview, modifyCareReviewInfo);
        CareReview updatedCareReview = careReviewPersist.save(modifiedReview);

        //TODO 댕글미터 재계산해서 영속

        return ReviewResp.builder()
                .reviewId(updatedCareReview.getCareReviewId())
                .reviewerId(updatedCareReview.getReviewerId())
                .revieweeId(updatedCareReview.getVetId())
                .build();
    }

    public ReviewResp deleteReview(Long reviewId) {
        CareReview savedCareReview = careReviewPersist.findBy(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        careReviewPersist.delete(savedCareReview);

        return ReviewResp.builder()
                .reviewId(savedCareReview.getCareReviewId())
                .reviewerId(savedCareReview.getReviewerId())
                .revieweeId(savedCareReview.getVetId())
                .build();
    }
}
