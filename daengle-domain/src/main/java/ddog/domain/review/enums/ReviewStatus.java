package ddog.domain.review.enums;

public enum ReviewStatus {
    REVIEW_SUBMITTED("리뷰 작성 완료");

    private final String description;

    ReviewStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
