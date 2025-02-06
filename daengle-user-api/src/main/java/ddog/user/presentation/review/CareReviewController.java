package ddog.user.presentation.review;

import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.user.application.ReservationService;
import ddog.user.presentation.reservation.dto.ReservationInfo;
import ddog.user.presentation.review.dto.response.CareReviewListResp;
import ddog.user.presentation.review.dto.request.UpdateCareReviewInfo;
import ddog.user.presentation.review.dto.request.PostCareReviewInfo;
import ddog.user.application.CareReviewService;
import ddog.user.presentation.review.dto.response.CareReviewDetailResp;
import ddog.user.presentation.review.dto.response.ReviewResp;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class CareReviewController {

    private final CareReviewService careReviewService;
    private final ReservationService reservationService;
    private final Environment environment;

    @GetMapping("/care/review/{reviewId}")
    public CommonResponseEntity<CareReviewDetailResp> findReview(@PathVariable Long reviewId) {
        return success(careReviewService.findReview(reviewId));
    }

    @PostMapping("/care/review")
    public CommonResponseEntity<ReviewResp> postReview(@RequestBody PostCareReviewInfo postCareReviewInfo) {
        ReservationInfo.ReservationUsersInfo findReservationInfo= reservationService.getCareUserAndPartnerDetail(postCareReviewInfo.getReservationId());
        //kakaoNotificationService.sendOneTalk(findReservationInfo.getPartnerName(), findReservationInfo.getPartnerPhone(), environment.getProperty("templateId.REVIEWED"));
        return success(careReviewService.postReview(postCareReviewInfo));
    }

    @PatchMapping("care/review/{reviewId}")
    public CommonResponseEntity<ReviewResp> updateReview(@PathVariable Long reviewId,
                                                         @RequestBody UpdateCareReviewInfo updateCareReviewInfo) {
        return success(careReviewService.updateReview(reviewId, updateCareReviewInfo));
    }

    @DeleteMapping("/care/review/{reviewId}")
    public CommonResponseEntity<ReviewResp> deleteReview(@PathVariable Long reviewId) {
        return success(careReviewService.deleteReview(reviewId));
    }

    @GetMapping("care/my-review/list")
    public CommonResponseEntity<CareReviewListResp> findMyReviewList(PayloadDto payloadDto,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return success(careReviewService.findMyReviewList(payloadDto.getAccountId(), page, size));
    }

    @GetMapping("/vet/{vetId}/review/list")
    public CommonResponseEntity<CareReviewListResp> findVetReviewList(
            @PathVariable Long vetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return success(careReviewService.findVetReviewList(vetId, page, size));
    }
}
