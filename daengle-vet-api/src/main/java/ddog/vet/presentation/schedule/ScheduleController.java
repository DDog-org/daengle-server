package ddog.vet.presentation.schedule;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.vet.application.ScheduleInfoService;
import ddog.vet.presentation.schedule.dto.ScheduleResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vet")
public class ScheduleController {
    private final ScheduleInfoService scheduleInfoService;

    @GetMapping("/schedule")
    public CommonResponseEntity<ScheduleResp> getVetSchedule(@AuthPayload PayloadDto payloadDto) {
        return success(scheduleInfoService.getScheduleByVetAccountId(payloadDto.getAccountId()));
    }

}
