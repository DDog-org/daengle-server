package ddog.user.application;

import ddog.domain.groomer.Groomer;
import ddog.domain.payment.Reservation;
import ddog.domain.review.GroomingReview;
import ddog.domain.review.dto.ModifyGroomingReviewInfo;
import ddog.domain.review.dto.PostGroomingReviewInfo;
import ddog.domain.user.User;
import ddog.persistence.mysql.port.GroomerPersist;
import ddog.persistence.mysql.port.GroomingReviewPersist;
import ddog.persistence.mysql.port.ReservationPersist;
import ddog.user.application.exception.account.UserException;
import ddog.user.application.exception.account.UserExceptionType;
import ddog.user.application.exception.estimate.ReservationException;
import ddog.user.application.exception.estimate.ReservationExceptionType;
import ddog.user.application.exception.ReviewException;
import ddog.user.application.exception.ReviewExceptionType;
import ddog.persistence.mysql.port.UserPersist;
import ddog.user.presentation.review.dto.GroomingReviewDetailResp;
import ddog.user.presentation.review.dto.GroomingReviewSummaryResp;
import ddog.user.presentation.review.dto.ReviewResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroomingReviewService {

    private final GroomingReviewPersist groomingReviewPersist;
    private final ReservationPersist reservationPersist;
    private final GroomerPersist groomerPersist;
    private final UserPersist userPersist;

    public ReviewResp postReview(PostGroomingReviewInfo postGroomingReviewInfo) {
        Reservation reservation = reservationPersist.findBy(postGroomingReviewInfo.getReservationId()).orElseThrow(()
                -> new ReservationException(ReservationExceptionType.RESERVATION_NOT_FOUND));

        validatePostGroomingReviewInfoDataFormat(postGroomingReviewInfo);

        GroomingReview groomingReviewToSave = GroomingReview.createBy(reservation, postGroomingReviewInfo);
        GroomingReview SavedGroomingReview = groomingReviewPersist.save(groomingReviewToSave);

        //TODO 댕글미터 계산해서 영속

        return ReviewResp.builder()
                .reviewId(SavedGroomingReview.getGroomingReviewId())
                .reviewerId(SavedGroomingReview.getReviewerId())
                .revieweeId(SavedGroomingReview.getGroomerId())
                .build();
    }

    public ReviewResp modifyReview(Long reviewId, ModifyGroomingReviewInfo modifyGroomingReviewInfo) {
        GroomingReview savedGroomingReview = groomingReviewPersist.findBy(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        validateModifyGroomingReviewInfoDataFormat(modifyGroomingReviewInfo);

        GroomingReview modifiedReview = GroomingReview.modifyBy(savedGroomingReview, modifyGroomingReviewInfo);
        GroomingReview updatedGroomingReview = groomingReviewPersist.save(modifiedReview);

        //TODO 댕글미터 재계산해서 영속

        return ReviewResp.builder()
                .reviewId(updatedGroomingReview.getGroomingReviewId())
                .reviewerId(updatedGroomingReview.getReviewerId())
                .revieweeId(updatedGroomingReview.getGroomerId())
                .build();
    }

    public GroomingReviewDetailResp getReview(Long reviewId) {
        GroomingReview savedCareReview = groomingReviewPersist.findBy(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        return GroomingReviewDetailResp.builder()
                .groomingReviewId(savedCareReview.getGroomingReviewId())
                .groomerId(savedCareReview.getGroomerId())
                .groomingKeywordReviewList(savedCareReview.getGroomingKeywordReviewList())
                .revieweeName(savedCareReview.getRevieweeName())
                .starRating(savedCareReview.getStarRating())
                .content(savedCareReview.getContent())
                .imageUrlList(savedCareReview.getImageUrlList())
                .build();
    }

    public ReviewResp deleteReview(Long reviewId) {
        GroomingReview savedGroomingReview = groomingReviewPersist.findBy(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        groomingReviewPersist.delete(savedGroomingReview);

        //TODO 댕글미터 재계산해서 영속

        return ReviewResp.builder()
                .reviewId(savedGroomingReview.getGroomingReviewId())
                .reviewerId(savedGroomingReview.getReviewerId())
                .revieweeId(savedGroomingReview.getGroomerId())
                .build();
    }

    public List<GroomingReviewSummaryResp> findMyReviewList(Long accountId, int page, int size) {
        User savedUser = userPersist.findBy(accountId)
                .orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

        Page<GroomingReview> groomingReviews = groomingReviewPersist.findByReviewerId(savedUser.getUserId(), pageable);

        return groomingReviews.stream().map(groomingReview ->
                GroomingReviewSummaryResp.builder()
                        .groomingReviewId(groomingReview.getGroomingReviewId())
                        .groomerId(groomingReview.getGroomerId())
                        .groomingKeywordReviewList(groomingReview.getGroomingKeywordReviewList())
                        .revieweeName(groomingReview.getRevieweeName())
                        .starRating(groomingReview.getStarRating())
                        .content(groomingReview.getContent())
                        .build()
        ).toList();
    }

    public List<GroomingReviewSummaryResp> findGroomerReviewList(Long groomerId, int page, int size) {
        Groomer savedGroomer = groomerPersist.findBy(groomerId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEWWEE_NOT_FOUNT));

        Pageable pageable = PageRequest.of(page, size);
        Page<GroomingReview> groomingReviews = groomingReviewPersist.findByGroomerId(savedGroomer.getGroomerId(), pageable);

        return groomingReviews.stream().map(groomingReview ->
                GroomingReviewSummaryResp.builder()
                        .groomingReviewId(groomingReview.getGroomingReviewId())
                        .groomerId(groomingReview.getGroomerId())
                        .groomingKeywordReviewList(groomingReview.getGroomingKeywordReviewList())
                        .revieweeName(groomingReview.getRevieweeName())
                        .starRating(groomingReview.getStarRating())
                        .content(groomingReview.getContent())
                        .build()
        ).toList();
    }

    private void validatePostGroomingReviewInfoDataFormat(PostGroomingReviewInfo postGroomingReviewInfo) {
        GroomingReview.validateStarRating(postGroomingReviewInfo.getStarRating());
        GroomingReview.validateGroomingKeywordReviewList(postGroomingReviewInfo.getGroomingKeywordReviewList());
        GroomingReview.validateContent(postGroomingReviewInfo.getContent());
        GroomingReview.validateImageUrlList(postGroomingReviewInfo.getImageUrlList());
    }

    private void validateModifyGroomingReviewInfoDataFormat(ModifyGroomingReviewInfo modifyGroomingReviewInfo) {
        GroomingReview.validateStarRating(modifyGroomingReviewInfo.getStarRating());
        GroomingReview.validateGroomingKeywordReviewList(modifyGroomingReviewInfo.getGroomingKeywordReviewList());
        GroomingReview.validateContent(modifyGroomingReviewInfo.getContent());
        GroomingReview.validateImageUrlList(modifyGroomingReviewInfo.getImageUrlList());
    }
}
