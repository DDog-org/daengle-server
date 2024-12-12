package ddog.groomer.presentation.estimate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ddog.domain.estimate.Proposal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class EstimateInfo {

    @Getter
    @Builder
    public static class General {

        private List<Content> estimates;

        @Getter
        @Builder
        public static class Content {
            private Long id;
            private String imageURL;
            private String nickname;
            private Proposal proposal;
            private String significant;

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            private LocalDateTime reservedDate;
        }
    }

    @Getter
    @Builder
    public static class Designation {

        private List<Content> estimates;

        @Getter
        @Builder
        public static class Content {
            private Long id;
            private String imageURL;
            private String nickname;
            private Proposal proposal;
            private String significant;

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
            private LocalDateTime reservedDate;
        }
    }
}
