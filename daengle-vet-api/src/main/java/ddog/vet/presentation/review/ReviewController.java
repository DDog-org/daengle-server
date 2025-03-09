package ddog.vet.presentation.review;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.vet.application.ReviewService;
import ddog.vet.presentation.review.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vet/review")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/list")
    public CommonResponseEntity<ReviewListResp> findReviewList(@AuthPayload PayloadDto payloadDto,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return success(reviewService.findReviewList(payloadDto.getAccountId(), page, size));
    }

    @GetMapping("/report/{careReviewId}")
    public CommonResponseEntity<ReportReviewResp> reportReview(@PathVariable Long careReviewId) {
        return success(reviewService.reportReview(careReviewId));
    }

    @PostMapping("/report")
    public CommonResponseEntity<SubmitReportReviewResp> reportReview(@RequestBody ReportReviewReq reportReviewReq) {
        return success(reviewService.reportReview(reportReviewReq));
    }

    @GetMapping("/report/list")
    public CommonResponseEntity<ReportedReviewListResp> findReportedReviewList(@AuthPayload PayloadDto payloadDto,
                                                                               @RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        return success(reviewService.findReportedReviewList(payloadDto.getAccountId(), page, size));
    }
}
