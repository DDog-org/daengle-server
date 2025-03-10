package ddog.user.presentation.estimate;

import ddog.auth.annotation.AuthPayload;
import ddog.auth.dto.PayloadDto;
import ddog.auth.exception.common.CommonResponseEntity;
import ddog.domain.estimate.dto.PetInfos;
import ddog.user.application.EstimateService;
import ddog.user.presentation.estimate.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static ddog.auth.exception.common.CommonResponseEntity.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/estimate")
public class EstimateController {

    private final EstimateService estimateService;

    /* 미용사, 사용자 및 반려견 정보 제공 */
    @PostMapping("/groomer-user-info")
    public CommonResponseEntity<UserInfo.Grooming> getGroomerAndUserInfo(@RequestBody GroomerInfoReq request, PayloadDto payloadDto) {
        return success(estimateService.getGroomerAndUserInfo(request.getGroomerId(), payloadDto.getAccountId()));
    }

    /* 병원, 사용자 및 반려견 정보 제공 */
    @PostMapping("/vet-user-info")
    public CommonResponseEntity<UserInfo.Care> getVetAndUserInfo(@RequestBody VetInfoReq request, PayloadDto payloadDto) {
        return success(estimateService.getVetAndUserInfo(request.getVetId(), payloadDto.getAccountId()));
    }

    /* 사용자 -> 미용사 (신규) 미용 견적서 등록 */
    @PostMapping("/grooming")
    public CommonResponseEntity<EstimateResp> createGroomingEstimate(@RequestBody CreateNewGroomingEstimateReq request, PayloadDto payloadDto) {
        return success(estimateService.createNewGroomingEstimate(request, payloadDto.getAccountId()));
    }

    /* 사용자 -> 병원 (신규) 진료 견적서 등록 */
    @PostMapping("/care")
    public CommonResponseEntity<EstimateResp> createCareEstimate(@RequestBody CreateNewCareEstimateReq request, PayloadDto payloadDto) {
        return success(estimateService.createNewCareEstimate(request, payloadDto.getAccountId()));
    }

    /* (일반) 대기 미용 견적서 페이지 반려동물 정보 반환 */
    @GetMapping("/general/grooming/pets")
    public CommonResponseEntity<EstimateInfo.Pet> findGeneralGroomingPets(@AuthPayload PayloadDto payloadDto) {
        return success(estimateService.findGeneralGroomingPets(payloadDto.getAccountId()));
    }

    /* (일반) 대기 진료 견적서 페이지 반려동물 정보 반환 */
    @GetMapping("/general/care/pets")
    public CommonResponseEntity<EstimateInfo.Pet> findGeneralCarePets(@AuthPayload PayloadDto payloadDto) {
        return success(estimateService.findGeneralCarePets(payloadDto.getAccountId()));
    }

    /* (일반) 대기 진료 견적서 페이지 반려동물 정보 반환 SQL 튜닝 버젼 */
    @GetMapping("/general/care/pets/tuning")
    public CommonResponseEntity<PetInfos> findTuningGeneralCarePets(@AuthPayload PayloadDto payloadDto) {
        return success(estimateService.findTuningGeneralCarePets(payloadDto.getAccountId()));
    }

    /* (일반) 대기 미용 견적서 리스트 조회 */
    @GetMapping("/general/grooming/{petId}")
    public CommonResponseEntity<EstimateInfo.Grooming> findGeneralGroomingEstimates(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthPayload PayloadDto payloadDto
    ) {
        return success(estimateService.findGeneralGroomingEstimates(petId, page, size, payloadDto.getAccountId()));
    }

    /* (일반) 대기 진료 견적서 리스트 조회 */
    @GetMapping("/general/care/{petId}")
    public CommonResponseEntity<EstimateInfo.Care> findGeneralCareEstimates(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthPayload PayloadDto payloadDto
    ) {
        return success(estimateService.findGeneralCareEstimates(petId, page, size, payloadDto.getAccountId()));
    }

    /* (지정) 대기 미용 견적서 페이지 반려동물 정보 반환 */
    @GetMapping("/designation/grooming/pets")
    public CommonResponseEntity<EstimateInfo.Pet> findDesignationGroomingPets(@AuthPayload PayloadDto payloadDto) {
        return success(estimateService.findDesignationGroomingPets(payloadDto.getAccountId()));
    }

    /* (지정) 대기 진료 견적서 페이지 반려동물 정보 반환 */
    @GetMapping("/designation/care/pets")
    public CommonResponseEntity<EstimateInfo.Pet> findDesignationCarePets(@AuthPayload PayloadDto payloadDto) {
        return success(estimateService.findDesignationCarePets(payloadDto.getAccountId()));
    }

    /* (지정) 대기 미용 견적서 리스트 조회 */
    @GetMapping("/designation/grooming/{petId}")
    public CommonResponseEntity<EstimateInfo.Grooming> findDesignationGroomingEstimates(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthPayload PayloadDto payloadDto
    ) {
        return success(estimateService.findDesignationGroomingEstimates(petId, page, size, payloadDto.getAccountId()));
    }

    /* (지정) 대기 진료 견적서 리스트 조회 */
    @GetMapping("/designation/care/{petId}")
    public CommonResponseEntity<EstimateInfo.Care> findDesignationCareEstimates(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthPayload PayloadDto payloadDto
    ) {
        return success(estimateService.findDesignationCareEstimates(petId, page, size, payloadDto.getAccountId()));
    }

    /* 사용자가 작성한 미용 견적서 조회 */
    @GetMapping("/request/grooming/{estimateId}")
    public CommonResponseEntity<UserEstimate.Grooming> getGroomingEstimate(@PathVariable Long estimateId) {
        return success(estimateService.getGroomingEstimate(estimateId));
    }

    /* 사용자가 작성한 진료 견적서 조회 */
    @GetMapping("/request/care/{estimateId}")
    public CommonResponseEntity<UserEstimate.Care> getCareEstimate(@PathVariable Long estimateId) {
        return success(estimateService.getCareEstimate(estimateId));
    }

    /* 미용 견적서 취소하기 */
    @PostMapping("/cancel/grooming")
    public CommonResponseEntity<EstimateResp> cancelGroomingEstimate(@RequestBody CancelEstimateReq request) {
        return success(estimateService.cancelGroomingEstimate(request.getEstimateId()));
    }

    /* 진료 견적서 취소하기 */
    @PostMapping("/cancel/care")
    public CommonResponseEntity<EstimateResp> cancelCareEstimate(@RequestBody CancelEstimateReq request) {
        return success(estimateService.cancelCareEstimate(request.getEstimateId()));
    }

    /* (대기) 미용 견적서 상세 조회 */
    @GetMapping("/{groomingEstimateId}/grooming-detail")
    public CommonResponseEntity<GroomingEstimateDetail> getGroomingEstimateDetail(@PathVariable Long groomingEstimateId,
                                                                                  @AuthPayload PayloadDto payloadDto) {
        return success(estimateService.getGroomingEstimateDetail(groomingEstimateId, payloadDto.getAccountId()));
    }

    /* (대기) 진료 견적서 상세 조회 */
    @GetMapping("/{careEstimateId}/care-detail")
    public CommonResponseEntity<CareEstimateDetail> getCareEstimateDetail(@PathVariable Long careEstimateId,
                                                                          @AuthPayload PayloadDto payloadDto) {
        return success(estimateService.getCareEstimateDetail(careEstimateId, payloadDto.getAccountId()));
    }
}
