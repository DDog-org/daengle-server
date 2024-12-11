package ddog.user.presentation.review.dto.response;

import ddog.domain.review.enums.GroomingKeywordReview;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GroomingReviewSummaryResp {
    private Long groomingReviewId;
    private Long groomerId;
    private String reviewerName;
    private String reviewerImageUrl;
    private List<GroomingKeywordReview> groomingKeywordReviewList;
    private String revieweeName;
    private double starRating;
    private String content;
    private List<String> imageUrlList;
}