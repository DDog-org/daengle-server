package ddog.groomer.presentation.estimate;

import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.groomer.application.EstimateService;
import ddog.groomer.presentation.estimate.dto.CreatePendingEstimateReq;
import ddog.groomer.presentation.estimate.dto.EstimateDetail;
import ddog.groomer.presentation.estimate.dto.EstimateInfo;
import ddog.groomer.presentation.estimate.dto.EstimateResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequestMapping("/api/groomer/estimate")
@RequiredArgsConstructor
public class EstimateController {

    private final EstimateService estimateService;

    /* (신규) 미용 견적서들 목록 조회 */
    @GetMapping("/list")
    public CommonResponseEntity<EstimateInfo> findEstimates(PayloadDto payloadDto) {
        return success(estimateService.findEstimates(payloadDto.getAccountId()));
    }

    /* (신규) 일반 견적서들 리스트 조회 */
    @GetMapping("/general")
    public CommonResponseEntity<EstimateInfo.General> findGeneralEstimates(
            PayloadDto payloadDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return success(estimateService.findGeneralEstimates(payloadDto.getAccountId(), page, size));
    }

    /* (신규) 지정 견적서들 리스트 조회 */
    @GetMapping("/designation")
    public CommonResponseEntity<EstimateInfo.Designation> findDesignationEstimates(PayloadDto payloadDto) {
        return success(estimateService.findDesignationEstimates(payloadDto.getAccountId()));
    }

    /* (신규) 미용 견적서 상세 조회 */
    @GetMapping("/{estimateId}/detail")
    public CommonResponseEntity<EstimateDetail> getEstimateDetail(@PathVariable Long estimateId) {
        return success(estimateService.getEstimateDetail(estimateId));
    }

    /* 미용사 -> 사용자 (대기) 미용 견적서 작성 */
    @PostMapping
    public CommonResponseEntity<EstimateResp> createEstimate(@RequestBody CreatePendingEstimateReq request, PayloadDto payloadDto) {
        return success(estimateService.createPendingEstimate(request, payloadDto.getAccountId()));
    }
}
