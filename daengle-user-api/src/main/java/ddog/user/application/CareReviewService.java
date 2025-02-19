package ddog.user.application;

import ddog.domain.filtering.BanWordValidator;
import ddog.domain.payment.Reservation;
import ddog.domain.payment.enums.ServiceType;
import ddog.domain.payment.port.ReservationPersist;
import ddog.domain.review.CareReview;
import ddog.domain.review.port.CareReviewPersist;
import ddog.domain.user.User;
import ddog.domain.user.port.UserPersist;
import ddog.domain.vet.Vet;
import ddog.domain.vet.VetDaengleMeter;
import ddog.domain.vet.VetKeyword;
import ddog.domain.vet.enums.CareBadge;
import ddog.domain.vet.enums.CareKeyword;
import ddog.domain.vet.port.VetDaengleMeterPersist;
import ddog.domain.vet.port.VetKeywordPersist;
import ddog.domain.vet.port.VetPersist;
import ddog.user.application.adapter.web.out.ReviewEventPublisher;
import ddog.user.application.dto.event.ReviewEvent;
import ddog.user.application.exception.ReviewException;
import ddog.user.application.exception.ReviewExceptionType;
import ddog.user.application.exception.account.UserException;
import ddog.user.application.exception.account.UserExceptionType;
import ddog.user.application.exception.account.VetException;
import ddog.user.application.exception.account.VetExceptionType;
import ddog.user.application.exception.estimate.ReservationException;
import ddog.user.application.exception.estimate.ReservationExceptionType;
import ddog.user.application.mapper.CareReviewMapper;
import ddog.user.application.mapper.EventMapper;
import ddog.user.presentation.review.dto.request.PostCareReviewInfo;
import ddog.user.presentation.review.dto.request.UpdateCareReviewInfo;
import ddog.user.presentation.review.dto.response.CareReviewDetailResp;
import ddog.user.presentation.review.dto.response.CareReviewListResp;
import ddog.user.presentation.review.dto.response.CareReviewSummaryResp;
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
public class CareReviewService {

    private final VetPersist vetPersist;
    private final UserPersist userPersist;
    private final VetKeywordPersist vetKeywordPersist;
    private final CareReviewPersist careReviewPersist;
    private final ReservationPersist reservationPersist;
    private final VetDaengleMeterPersist vetDaengleMeterPersist;

    private final BanWordValidator banWordValidator;
    private final ReviewEventPublisher reviewEventPublisher;

    @Transactional(readOnly = true)
    public CareReviewDetailResp findReview(Long reviewId) {
        CareReview savedCareReview = careReviewPersist.findByReviewId(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        Reservation savedReservation = reservationPersist.findByReservationId(savedCareReview.getReservationId())
                .orElseThrow(() -> new ReservationException(ReservationExceptionType.RESERVATION_NOT_FOUND));

        return CareReviewDetailResp.builder()
                .careReviewId(savedCareReview.getCareReviewId())
                .reservationId(savedReservation.getReservationId())
                .vetId(savedCareReview.getVetId())
                .careKeywordList(savedCareReview.getCareKeywordList())
                .revieweeName(savedCareReview.getRevieweeName())
                .shopName(savedCareReview.getShopName())
                .starRating(savedCareReview.getStarRating())
                .schedule(savedReservation.getSchedule())
                .content(savedCareReview.getContent())
                .imageUrlList(savedCareReview.getImageUrlList())
                .build();
    }

    @Transactional
    public ReviewResp postReview(PostCareReviewInfo postCareReviewInfo) {
        Reservation reservation = reservationPersist.findByReservationId(postCareReviewInfo.getReservationId()).orElseThrow(()
                -> new ReservationException(ReservationExceptionType.RESERVATION_NOT_FOUND));

        Vet savedVet = vetPersist.findByVetId(reservation.getRecipientId())
                .orElseThrow(() -> new VetException(VetExceptionType.VET_NOT_FOUND));

        if (careReviewPersist.findByReservationId(postCareReviewInfo.getReservationId())
                .isPresent()) throw new ReviewException(ReviewExceptionType.REVIEW_HAS_WRITTEN);

        if (reservation.getServiceType() != ServiceType.CARE)
            throw new ReviewException(ReviewExceptionType.REVIEW_INVALID_SERVICE_TYPE);

        validatePostCareReviewInfoDataFormat(postCareReviewInfo);

        String includedBanWord = banWordValidator.findBanWords(postCareReviewInfo.getContent());
        if (includedBanWord != null)
            throw new ReviewException(ReviewExceptionType.REVIEW_CONTENT_CONTAIN_BAN_WORD, includedBanWord);

        CareReview careReviewToSave = CareReviewMapper.createBy(reservation, postCareReviewInfo);
        CareReview savedCareReview = careReviewPersist.save(careReviewToSave);

        processKeywords(postCareReviewInfo, savedVet);

        VetDaengleMeter vetDaengleMeter = vetDaengleMeterPersist.findByVetId(savedCareReview.getVetId())
                .orElseThrow(() -> new VetException(VetExceptionType.VET_DAENGLE_METER_NOT_FOUND));

        Integer daengleScore = convertStarRatingToDaengleScore(postCareReviewInfo.getStarRating());

        vetDaengleMeter.updateMeterForNewReview(daengleScore);
        vetDaengleMeterPersist.save(vetDaengleMeter);

        savedVet.updateDaengleMeter(vetDaengleMeter.getScore());
        vetPersist.save(savedVet);

        //이벤트 발행
        ReviewEvent reviewEvent = EventMapper.createBy(savedCareReview, savedVet);
        reviewEventPublisher.publishEvent(reviewEvent);

        return ReviewResp.builder()
                .reviewId(savedCareReview.getCareReviewId())
                .reviewerId(savedCareReview.getReviewerId())
                .revieweeId(savedCareReview.getVetId())
                .banWord(null)
                .build();
    }

    private void processKeywords(PostCareReviewInfo postCareReviewInfo, Vet vet) {

        List<VetKeyword> vetKeywords = new ArrayList<>(vet.getKeywords());
        List<CareBadge> badges = new ArrayList<>(vet.getBadges());

        List<CareKeyword> postKeywords = postCareReviewInfo.getCareKeywordList();
        for (CareKeyword postKeyword : postKeywords) {
            boolean isAdded = false;

            for (VetKeyword vetKeyword : vetKeywords) {
                if (postKeyword.toString().equals(vetKeyword.getKeyword())) {
                    vetKeyword.increaseCount();
                    vetKeywordPersist.save(vetKeyword);

                    if (vetKeyword.isAvailableRegisterBadge()) {
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

            VetKeyword newKeyword = VetKeyword.createNewKeyword(vet.getAccountId(), postKeyword.toString());
            VetKeyword savedKeyword = vetKeywordPersist.save(newKeyword);
            vetKeywords.add(savedKeyword);
        }
        vet.updateKeywords(vetKeywords);
        vet.updateBadges(badges);
    }

    @Transactional
    public ReviewResp updateReview(Long reviewId, UpdateCareReviewInfo updateCareReviewInfo) {
        CareReview savedCareReview = careReviewPersist.findByReviewId(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        Vet savedVet = vetPersist.findByVetId(savedCareReview.getVetId())
                .orElseThrow(() -> new VetException(VetExceptionType.VET_NOT_FOUND));

        validateModifyCareReviewInfoDataFormat(updateCareReviewInfo);

        String includedBanWord = banWordValidator.findBanWords(updateCareReviewInfo.getContent());
        if (includedBanWord != null)
            throw new ReviewException(ReviewExceptionType.REVIEW_CONTENT_CONTAIN_BAN_WORD, includedBanWord);

        CareReview modifiedReview = CareReviewMapper.modifyBy(savedCareReview, updateCareReviewInfo);
        CareReview updatedCareReview = careReviewPersist.save(modifiedReview);

        VetDaengleMeter vetDaengleMeter = vetDaengleMeterPersist.findByVetId(savedVet.getVetId())
                .orElseThrow(() -> new VetException(VetExceptionType.VET_DAENGLE_METER_NOT_FOUND));

        Integer oldScore = convertStarRatingToDaengleScore(savedCareReview.getStarRating());
        Integer newScore = convertStarRatingToDaengleScore(updateCareReviewInfo.getStarRating());

        vetDaengleMeter.updateMeterForModifiedReview(oldScore, newScore);
        vetDaengleMeterPersist.save(vetDaengleMeter);

        savedVet.updateDaengleMeter(vetDaengleMeter.getScore());
        vetPersist.save(savedVet);

        return ReviewResp.builder()
                .reviewId(updatedCareReview.getCareReviewId())
                .reviewerId(updatedCareReview.getReviewerId())
                .revieweeId(updatedCareReview.getVetId())
                .banWord(null)
                .build();
    }

    @Transactional
    public ReviewResp deleteReview(Long reviewId) {
        CareReview savedCareReview = careReviewPersist.findByReviewId(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEW_NOT_FOUND));

        Vet savedVet = vetPersist.findByVetId(savedCareReview.getVetId())
                .orElseThrow(() -> new VetException(VetExceptionType.VET_NOT_FOUND));

        careReviewPersist.delete(savedCareReview);

        VetDaengleMeter vetDaengleMeter = vetDaengleMeterPersist.findByVetId(savedVet.getVetId())
                .orElseThrow(() -> new VetException(VetExceptionType.VET_DAENGLE_METER_NOT_FOUND));

        Integer daengleScore = convertStarRatingToDaengleScore(savedCareReview.getStarRating());

        vetDaengleMeter.updateMeterForDeletedReview(daengleScore);
        vetDaengleMeterPersist.save(vetDaengleMeter);

        savedVet.updateDaengleMeter(daengleScore);
        vetPersist.save(savedVet);

        return ReviewResp.builder()
                .reviewId(savedCareReview.getCareReviewId())
                .reviewerId(savedCareReview.getReviewerId())
                .revieweeId(savedCareReview.getVetId())
                .build();
    }

    @Transactional(readOnly = true)
    public CareReviewListResp findMyReviewList(Long accountId, int page, int size) {
        User savedUser = userPersist.findByAccountId(accountId)
                .orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<CareReview> careReviews = careReviewPersist.findByReviewerId(savedUser.getAccountId(), pageable);

        return mappingToCareReviewListResp(careReviews);
    }

    public CareReviewListResp findVetReviewList(Long vetId, int page, int size) {
        Vet savedVet = vetPersist.findByVetId(vetId)
                .orElseThrow(() -> new ReviewException(ReviewExceptionType.REVIEWWEE_NOT_FOUNT));

        Pageable pageable = PageRequest.of(page, size);
        Page<CareReview> careReviews = careReviewPersist.findByVetId(savedVet.getVetId(), pageable);

        return mappingToCareReviewListResp(careReviews);
    }

    private void validatePostCareReviewInfoDataFormat(PostCareReviewInfo postCareReviewInfo) {
        CareReview.validateStarRating(postCareReviewInfo.getStarRating());
        CareReview.validateCareKeywordReviewList(postCareReviewInfo.getCareKeywordList());
        CareReview.validateContent(postCareReviewInfo.getContent());
        CareReview.validateImageUrlList(postCareReviewInfo.getImageUrlList());
    }

    private void validateModifyCareReviewInfoDataFormat(UpdateCareReviewInfo updateCareReviewInfo) {
        CareReview.validateStarRating(updateCareReviewInfo.getStarRating());
        CareReview.validateCareKeywordReviewList(updateCareReviewInfo.getCareKeywordList());
        CareReview.validateContent(updateCareReviewInfo.getContent());
        CareReview.validateImageUrlList(updateCareReviewInfo.getImageUrlList());
    }

    private CareReviewListResp mappingToCareReviewListResp(Page<CareReview> careReviews) {
        List<CareReviewSummaryResp> careReviewList = careReviews.stream().map(careReview -> {

            User reviewer = userPersist.findByAccountId(careReview.getReviewerId())
                    .orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

            reservationPersist.findByReservationId(careReview.getReservationId())
                    .orElseThrow(() -> new ReservationException(ReservationExceptionType.RESERVATION_NOT_FOUND));

            return CareReviewSummaryResp.builder()
                    .careReviewId(careReview.getCareReviewId())
                    .reviewerName(reviewer.getUsername())
                    .reviewerImageUrl(reviewer.getImageUrl())
                    .vetId(careReview.getVetId())
                    .careKeywordList(careReview.getCareKeywordList())
                    .revieweeName(careReview.getRevieweeName())
                    .createdAt(careReview.getCreatedAt())
                    .starRating(careReview.getStarRating())
                    .content(careReview.getContent())
                    .imageUrlList(careReview.getImageUrlList())
                    .build();
        }).toList(); // careReviewList

        return CareReviewListResp.builder()
                .reviewCount(careReviews.getTotalElements())
                .reviewList(careReviewList)
                .build();
    }

    private Integer convertStarRatingToDaengleScore(Integer starRating) {
        return starRating * 20;
    }
}
