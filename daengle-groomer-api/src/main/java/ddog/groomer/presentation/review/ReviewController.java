package ddog.groomer.presentation.review;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.groomer.application.ReviewService;
import ddog.groomer.presentation.review.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ddog.auth.exception.common.CommonResponseEntity.success;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groomer/review")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/list")
    public CommonResponseEntity<ReviewListResp> findReviewList(@AuthPayload PayloadDto payloadDto,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return success(reviewService.findReviewList(payloadDto.getAccountId(), page, size));
    }

    @GetMapping("/report/{groomingReviewId}")
    public CommonResponseEntity<ReportReviewResp> reportReview(@PathVariable Long groomingReviewId) {
        return success(reviewService.reportReview(groomingReviewId));
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
