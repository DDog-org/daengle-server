package ddog.user.presentation.reservation;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.user.application.ReservationService;
import ddog.user.presentation.reservation.dto.EstimateDetail;
import ddog.user.presentation.reservation.dto.ReservationInfo;
import ddog.user.presentation.reservation.dto.ReservationSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{reservationId}/review")
    public CommonResponseEntity<ReservationSummary> getReservationSummary(@PathVariable Long reservationId) {
        return success(reservationService.getReservationSummary(reservationId));
    }

    @GetMapping("/grooming/list")
    public CommonResponseEntity<ReservationInfo.Grooming> findGroomingEstimates(@AuthPayload PayloadDto payloadDto) {
        return success(reservationService.findGroomingEstimates(payloadDto.getAccountId()));
    }

    @GetMapping("/care/list")
    public CommonResponseEntity<ReservationInfo.Care> findCareEstimates(@AuthPayload PayloadDto payloadDto) {
        return success(reservationService.findCareEstimates(payloadDto.getAccountId()));
    }

    @GetMapping("/grooming/{estimateId}/detail")
    public CommonResponseEntity<EstimateDetail.Grooming> getGroomingEstimateDetail(@PathVariable Long estimateId,
                                                                                   @AuthPayload PayloadDto payloadDto) {
        return success(reservationService.getGroomingEstimateDetail(estimateId, payloadDto.getAccountId()));
    }

    @GetMapping("/care/{estimateId}/detail")
    public CommonResponseEntity<EstimateDetail.Care> getCareEstimateDetail(@PathVariable Long estimateId,
                                                                           @AuthPayload PayloadDto payloadDto) {
        return success(reservationService.getCareEstimateDetail(estimateId, payloadDto.getAccountId()));
    }
}
