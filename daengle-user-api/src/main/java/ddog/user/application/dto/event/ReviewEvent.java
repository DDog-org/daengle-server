package ddog.user.application.dto.event;

import ddog.domain.event.enums.EventType;
import ddog.domain.review.enums.ReviewStatus;

public record ReviewEvent(
    Long reviewId,
    Long reviewerId,
    String revieweeName,
    String reviewContent,
    String revieweePhoneNumber,
    ReviewStatus reviewStatus,
    EventType eventType
) {}
