package ddog.daengleserver.presentation.estimate.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import ddog.daengleserver.domain.account.enums.Weight;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCareEstimateDetail {

    private String userImage;
    private String nickname;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime reservedDate;

    private Long id;
    private String petImage;
    private String name;
    private int birth;
    private Weight weight;
    private String significant;
    private String symptoms;
    private String requirements;

}