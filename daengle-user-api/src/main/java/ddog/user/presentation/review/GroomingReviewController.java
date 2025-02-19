package ddog.user.presentation.review;

import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.user.presentation.review.dto.request.UpdateGroomingReviewInfo;
import ddog.user.presentation.review.dto.request.PostGroomingReviewInfo;
import ddog.user.application.GroomingReviewService;
import ddog.user.presentation.review.dto.response.GroomingReviewDetailResp;
import ddog.user.presentation.review.dto.response.GroomingReviewListResp;
import ddog.user.presentation.review.dto.response.ReviewResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class GroomingReviewController {

    private final GroomingReviewService groomingReviewService;

    @GetMapping("/grooming/review/{reviewId}")
    public CommonResponseEntity<GroomingReviewDetailResp> findReview(@PathVariable Long reviewId) {
        return success(groomingReviewService.findReview(reviewId));
    }

    @PostMapping("/grooming/review")
    public CommonResponseEntity<ReviewResp> postReview(@RequestBody PostGroomingReviewInfo postGroomingReviewInfo) {
        return success(groomingReviewService.postReview(postGroomingReviewInfo));
    }

    @PatchMapping("/grooming/review/{reviewId}")
    public CommonResponseEntity<ReviewResp> updateReview(@PathVariable Long reviewId,
                                                         @RequestBody UpdateGroomingReviewInfo updateGroomingReviewInfo) {
        return success(groomingReviewService.updateReview(reviewId, updateGroomingReviewInfo));
    }

    @DeleteMapping("/grooming/review/{reviewId}")
    public CommonResponseEntity<ReviewResp> deleteReview(@PathVariable Long reviewId) {
        return success(groomingReviewService.deleteReview(reviewId));
    }

    @GetMapping("/grooming/my-review/list")
    public CommonResponseEntity<GroomingReviewListResp> findMyReviewList(PayloadDto payloadDto,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        return success(groomingReviewService.findMyReviewList(payloadDto.getAccountId(), page, size));
    }

    @GetMapping("/groomer/{groomerId}/review/list")
    public CommonResponseEntity<GroomingReviewListResp> findGroomerReviewList(
            @PathVariable Long groomerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return success(groomingReviewService.findGroomerReviewList(groomerId, page, size));
    }
}
