package ddog.user.presentation.estimate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import ddog.domain.pet.Weight;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CareEstimateReq {
    private Long vetId;

    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime reservedDate;
    private Long id;
    private String petImage;
    private String name;
    private String significant;
    private int birth;
    private Weight weight;
    private String symptoms;
    private String requirements;
    private String userImage;
    private String nickname;

}
