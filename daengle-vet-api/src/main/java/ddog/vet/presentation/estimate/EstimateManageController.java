package ddog.vet.presentation.estimate;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.vet.application.EstimateManageService;
import ddog.vet.presentation.estimate.dto.PetInfo;
import ddog.vet.presentation.estimate.dto.ReservationEstimateContent;
import ddog.vet.presentation.estimate.dto.WeekScheduleResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequestMapping("/api/vet")
@RequiredArgsConstructor
public class EstimateManageController {
    private final EstimateManageService estimateManageService;

    @GetMapping("/reservation/{reservationId}")
    public CommonResponseEntity<ReservationEstimateContent> findReservationOrEstimateBy(@PathVariable Long reservationId,
                                                                                        @AuthPayload PayloadDto payloadDto) {
        return success(estimateManageService.findEstimateByVetAccountIdAndReservationId(payloadDto.getAccountId(), reservationId));
    }

    @GetMapping("/week/{date}")
    public CommonResponseEntity<WeekScheduleResp> findWeekSchedule(@AuthPayload PayloadDto payloadDto,
                                                                   @PathVariable String date){
        return success(estimateManageService.findScheduleByVetIdAndDate(payloadDto.getAccountId(), date));
    }

    @GetMapping("/petInfo/{petId}")
    public CommonResponseEntity<PetInfo> findPetInfoByPetId(@PathVariable Long petId) {
        return success(estimateManageService.findPetInfoByPetId(petId));
    }
}