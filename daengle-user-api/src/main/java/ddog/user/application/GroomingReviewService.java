package ddog.user.application;

import ddog.domain.filtering.BanWordValidator;
import ddog.domain.groomer.Groomer;
import ddog.domain.groomer.GroomerDaengleMeter;
import ddog.domain.groomer.GroomerKeyword;
import ddog.domain.groomer.enums.GroomingBadge;
import ddog.domain.groomer.enums.GroomingKeyword;
import ddog.domain.groomer.port.GroomerDaengleMeterPersist;
import ddog.domain.groomer.port.GroomerKeywordPersist;
import ddog.domain.groomer.port.GroomerPersist;
import ddog.domain.payment.Reservation;
import ddog.domain.payment.enums.ServiceType;
import ddog.domain.payment.port.ReservationPersist;
import ddog.domain.review.GroomingReview;
import ddog.domain.review.port.GroomingReviewPersist;
import ddog.domain.user.User;
import ddog.domain.user.port.UserPersist;
import ddog.user.application.adapter.web.out.ReviewEventPublisher;
import ddog.user.application.dto.event.ReviewEvent;
import ddog.user.application.exception.ReviewException;
import ddog.user.application.exception.ReviewExceptionType;
import ddog.user.application.exception.account.GroomerException;
import ddog.user.application.exception.account.GroomerExceptionType;
import ddog.user.application.exception.account.UserException;
import ddog.user.application.exception.account.UserExceptionType;
import ddog.user.application.exception.estimate.ReservationException;
import ddog.user.application.exception.estimate.ReservationExceptionType;
import ddog.user.application.mapper.EventMapper;
import ddog.user.application.mapper.GroomingReviewMapper;
import ddog.user.presentation.review.dto.request.PostGroomingReviewInfo;
import ddog.user.presentation.review.dto.request.UpdateGroomingReviewInfo;
import ddog.user.presentation.review.dto.response.GroomingReviewDetailResp;
import ddog.user.presentation.review.dto.response.GroomingReviewListResp;
import ddog.user.presentation.review.dto.response.GroomingReviewSummaryResp;
import ddog.user.presentation.review.dto.response.ReviewResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroomingReviewService {

    private final UserPersist userPersist;
    private final GroomerPersist groomerPersist;
    private final GroomerKeywordPersist groomerKeywordPersist;
    private final ReservationPersist reservationPersist;
    private final GroomingReviewPersist groomingReviewPersist;
    private final GroomerDaengleMeterPersist groomerDaengleMeterPersist;

    private final BanWordValidator banWordValidator;
    private final ReviewEventPublisher reviewEventPublisher;

    @Transactional(readOnly = true)
    public GroomingReviewDetailResp findReview(Long reviewId) {
        GroomingReview savedGroomingReview = groomingReviewPersist.findByReviewId(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        Reservation savedReservation = reservationPersist.findByReservationId(savedGroomingReview.getReservationId())
                .orElseThrow(() -> new ReservationException(ReservationExceptionType.RESERVATION_NOT_FOUND));

        return GroomingReviewDetailResp.builder()
                .groomingReviewId(savedGroomingReview.getGroomingReviewId())
                .reservationId(savedReservation.getReservationId())
                .groomerId(savedGroomingReview.getGroomerId())
                .groomingKeywordList(savedGroomingReview.getGroomingKeywordList())
                .revieweeName(savedGroomingReview.getRevieweeName())
                .shopName(savedGroomingReview.getShopName())
                .starRating(savedGroomingReview.getStarRating())
                .schedule(savedReservation.getSchedule())
                .content(savedGroomingReview.getContent())
                .imageUrlList(savedGroomingReview.getImageUrlList())
                .build();
    }

    @Transactional
    public ReviewResp postReview(PostGroomingReviewInfo postGroomingReviewInfo) {
        Reservation reservation = reservationPersist.findByReservationId(postGroomingReviewInfo.getReservationId())
                .orElseThrow(() -> new ReservationException(ReservationExceptionType.RESERVATION_NOT_FOUND));

        Groomer savedGroomer = groomerPersist.findByGroomerId(reservation.getRecipientId())
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_NOT_FOUND));

        if (reservation.getServiceType() != ServiceType.GROOMING)
            throw new ReviewException(ReviewExceptionType.REVIEW_INVALID_SERVICE_TYPE);

        if (groomingReviewPersist.findByReservationId(postGroomingReviewInfo.getReservationId())
                .isPresent()) throw new ReviewException(ReviewExceptionType.REVIEW_HAS_WRITTEN);

        validatePostGroomingReviewInfoDataFormat(postGroomingReviewInfo);

        String includedBanWord = banWordValidator.findBanWords(postGroomingReviewInfo.getContent());
        if (includedBanWord != null)
            throw new ReviewException(ReviewExceptionType.REVIEW_CONTENT_CONTAIN_BAN_WORD, includedBanWord);

        GroomingReview groomingReviewToSave = GroomingReviewMapper.createBy(reservation, postGroomingReviewInfo);
        GroomingReview savedGroomingReview = groomingReviewPersist.save(groomingReviewToSave);

        processKeywords(postGroomingReviewInfo, savedGroomer);

        GroomerDaengleMeter groomerDaengleMeter = groomerDaengleMeterPersist.findByGroomerId(savedGroomer.getGroomerId())
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_DAENGLE_METER_NOT_FOUND));

        Integer daengleScore = convertStarRatingToDaengleScore(postGroomingReviewInfo.getStarRating());

        groomerDaengleMeter.updateMeterForNewReview(daengleScore);
        groomerDaengleMeterPersist.save(groomerDaengleMeter);

        savedGroomer.updateDaengleMeter(groomerDaengleMeter.getScore());
        groomerPersist.save(savedGroomer);

        //이벤트 발행
        ReviewEvent reviewEvent = EventMapper.createBy(savedGroomingReview, savedGroomer);
        reviewEventPublisher.publishEvent(reviewEvent);

        return ReviewResp.builder()
                .reviewId(savedGroomingReview.getGroomingReviewId())
                .reviewerId(savedGroomingReview.getReviewerId())
                .revieweeId(savedGroomingReview.getGroomerId())
                .banWord(null)
                .build();
    }

    private void processKeywords(PostGroomingReviewInfo postGroomingReviewInfo, Groomer groomer) {

        List<GroomerKeyword> groomerKeywords = new ArrayList<>(groomer.getKeywords());
        List<GroomingBadge> badges = new ArrayList<>(groomer.getBadges());

        List<GroomingKeyword> postKeywords = postGroomingReviewInfo.getGroomingKeywordList();

        for (GroomingKeyword postKeyword : postKeywords) {
            boolean isAdded = false;
            
            for (GroomerKeyword groomerKeyword : groomerKeywords) {
                if (postKeyword.toString().equals(groomerKeyword.getKeyword())) {
                    groomerKeyword.increaseCount();
                    
                    if (groomerKeyword.isAvailableRegisterBadge()) {
                        if (postKeyword.getBadge() != null) {
                            badges.add(postKeyword.getBadge());
                        }
                    }
                    isAdded = true;
                    break;
                }
            }
            if (isAdded) {
                continue;
            }
            
            GroomerKeyword newKeyword = GroomerKeyword.createNewKeyword(groomer.getAccountId(), postKeyword.toString());
            GroomerKeyword savedKeyword = groomerKeywordPersist.save(newKeyword);
            groomerKeywords.add(savedKeyword);
        }

        groomer.updateKeywords(groomerKeywords);
        groomer.updateBadges(badges);
    }

    @Transactional
    public ReviewResp updateReview(Long reviewId, UpdateGroomingReviewInfo updateGroomingReviewInfo) {
        GroomingReview savedGroomingReview = groomingReviewPersist.findByReviewId(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        Groomer savedGroomer = groomerPersist.findByGroomerId(savedGroomingReview.getGroomerId())
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_NOT_FOUND));

        validateModifyGroomingReviewInfoDataFormat(updateGroomingReviewInfo);

        String includedBanWord = banWordValidator.findBanWords(updateGroomingReviewInfo.getContent());
        if (includedBanWord != null)
            throw new ReviewException(ReviewExceptionType.REVIEW_CONTENT_CONTAIN_BAN_WORD, includedBanWord);

        GroomingReview modifiedReview = GroomingReviewMapper.updateBy(savedGroomingReview, updateGroomingReviewInfo);
        GroomingReview updatedGroomingReview = groomingReviewPersist.save(modifiedReview);

        GroomerDaengleMeter groomerDaengleMeter = groomerDaengleMeterPersist.findByGroomerId(savedGroomer.getGroomerId())
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_DAENGLE_METER_NOT_FOUND));

        Integer oldScore = convertStarRatingToDaengleScore(savedGroomingReview.getStarRating());
        Integer newScore = convertStarRatingToDaengleScore(updateGroomingReviewInfo.getStarRating());

        groomerDaengleMeter.updateMeterForModifiedReview(oldScore, newScore);
        groomerDaengleMeterPersist.save(groomerDaengleMeter);

        savedGroomer.updateDaengleMeter(groomerDaengleMeter.getScore());
        groomerPersist.save(savedGroomer);

        return ReviewResp.builder()
                .reviewId(updatedGroomingReview.getGroomingReviewId())
                .reviewerId(updatedGroomingReview.getReviewerId())
                .revieweeId(updatedGroomingReview.getGroomerId())
                .banWord(null)
                .build();
    }

    @Transactional
    public ReviewResp deleteReview(Long reviewId) {
        GroomingReview savedGroomingReview = groomingReviewPersist.findByReviewId(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        Groomer savedGroomer = groomerPersist.findByGroomerId(savedGroomingReview.getGroomerId())
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_NOT_FOUND));

        groomingReviewPersist.delete(savedGroomingReview);

        GroomerDaengleMeter groomerDaengleMeter = groomerDaengleMeterPersist.findByGroomerId(savedGroomer.getGroomerId())
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_DAENGLE_METER_NOT_FOUND));

        Integer daengleScore = convertStarRatingToDaengleScore(savedGroomingReview.getStarRating());

        groomerDaengleMeter.updateMeterForDeletedReview(daengleScore);
        groomerDaengleMeterPersist.save(groomerDaengleMeter);

        savedGroomer.updateDaengleMeter(groomerDaengleMeter.getScore());
        groomerPersist.save(savedGroomer);

        return ReviewResp.builder()
                .reviewId(savedGroomingReview.getGroomingReviewId())
                .reviewerId(savedGroomingReview.getReviewerId())
                .revieweeId(savedGroomingReview.getGroomerId())
                .build();
    }

    @Transactional(readOnly = true)
    public GroomingReviewListResp findMyReviewList(Long accountId, int page, int size) {
        User savedUser = userPersist.findByAccountId(accountId)
                .orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<GroomingReview> groomingReviews = groomingReviewPersist.findByReviewerId(savedUser.getAccountId(), pageable);

        return mappingToGroomingReviewListResp(groomingReviews);
    }

    @Transactional(readOnly = true)
    public GroomingReviewListResp findGroomerReviewList(Long groomerId, int page, int size) {
        Groomer savedGroomer = groomerPersist.findByGroomerId(groomerId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEWWEE_NOT_FOUNT));

        Pageable pageable = PageRequest.of(page, size);
        Page<GroomingReview> groomingReviews = groomingReviewPersist.findByGroomerId(savedGroomer.getGroomerId(), pageable);

        return mappingToGroomingReviewListResp(groomingReviews);
    }

    private void validatePostGroomingReviewInfoDataFormat(PostGroomingReviewInfo postGroomingReviewInfo) {
        GroomingReview.validateStarRating(postGroomingReviewInfo.getStarRating());
        GroomingReview.validateGroomingKeywordReviewList(postGroomingReviewInfo.getGroomingKeywordList());
        GroomingReview.validateContent(postGroomingReviewInfo.getContent());
        GroomingReview.validateImageUrlList(postGroomingReviewInfo.getImageUrlList());
    }

    private void validateModifyGroomingReviewInfoDataFormat(UpdateGroomingReviewInfo updateGroomingReviewInfo) {
        GroomingReview.validateStarRating(updateGroomingReviewInfo.getStarRating());
        GroomingReview.validateGroomingKeywordReviewList(updateGroomingReviewInfo.getGroomingKeywordList());
        GroomingReview.validateContent(updateGroomingReviewInfo.getContent());
        GroomingReview.validateImageUrlList(updateGroomingReviewInfo.getImageUrlList());
    }

    private GroomingReviewListResp mappingToGroomingReviewListResp(Page<GroomingReview> groomingReviews) {
        List<GroomingReviewSummaryResp> groomingReviewList = groomingReviews.stream().map(groomingReview -> {

            User reviewer = userPersist.findByAccountId(groomingReview.getReviewerId())
                    .orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

            reservationPersist.findByReservationId(groomingReview.getReservationId())
                    .orElseThrow(() -> new ReservationException(ReservationExceptionType.RESERVATION_NOT_FOUND));

            return GroomingReviewSummaryResp.builder()
                    .groomingReviewId(groomingReview.getGroomingReviewId())
                    .reviewerName(reviewer.getNickname())
                    .reviewerImageUrl(reviewer.getImageUrl())
                    .groomerId(groomingReview.getGroomerId())
                    .groomingKeywordList(groomingReview.getGroomingKeywordList())
                    .revieweeName(groomingReview.getRevieweeName())
                    .createdAt(groomingReview.getCreatedAt())
                    .starRating(groomingReview.getStarRating())
                    .content(groomingReview.getContent())
                    .imageUrlList(groomingReview.getImageUrlList())
                    .build();
        }).toList(); // groomingReviewList

        return GroomingReviewListResp.builder()
                .reviewCount(groomingReviews.getTotalElements())
                .reviewList(groomingReviewList)
                .build();
    }

    private Integer convertStarRatingToDaengleScore(Integer starRating) {
        return starRating * 20;
    }
}
