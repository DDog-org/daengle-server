package ddog.user.presentation.search.dto;

import ddog.domain.groomer.enums.GroomingBadge;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroomerResult {
    private int page;
    private int size;
    private long totalElements;

    List<Results> result;

    @Getter
    @Builder
    public static class Results {
        public Long partnerId;
        public String partnerName;
        public String partnerImage;
        public List<GroomingBadge> groomingBadges;
    }
}
