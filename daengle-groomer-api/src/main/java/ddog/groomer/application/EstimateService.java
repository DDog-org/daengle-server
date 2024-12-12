package ddog.groomer.application;

import ddog.domain.estimate.EstimateStatus;
import ddog.domain.estimate.GroomingEstimate;
import ddog.domain.estimate.Proposal;
import ddog.domain.estimate.port.GroomingEstimatePersist;
import ddog.domain.groomer.Groomer;
import ddog.domain.groomer.port.GroomerPersist;
import ddog.domain.pet.Pet;
import ddog.domain.pet.port.PetPersist;
import ddog.domain.user.User;
import ddog.domain.user.port.UserPersist;
import ddog.groomer.application.exception.GroomingEstimateException;
import ddog.groomer.application.exception.GroomingEstimateExceptionType;
import ddog.groomer.application.exception.account.*;
import ddog.groomer.application.mapper.GroomingEstimateMapper;
import ddog.groomer.presentation.estimate.dto.CreatePendingEstimateReq;
import ddog.groomer.presentation.estimate.dto.EstimateDetail;
import ddog.groomer.presentation.estimate.dto.EstimateInfo;
import ddog.groomer.presentation.estimate.dto.EstimateResp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstimateService {

    private final GroomerPersist groomerPersist;
    private final PetPersist petPersist;
    private final UserPersist userPersist;

    private final GroomingEstimatePersist groomingEstimatePersist;

    @Transactional(readOnly = true)
    public EstimateInfo.General findGeneralEstimates(Long accountId, int page, int size) {
        Groomer groomer = groomerPersist.findByAccountId(accountId)
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

        Page<GroomingEstimate> estimates = groomingEstimatePersist.findByStatusAndProposalAndAddress(EstimateStatus.NEW, Proposal.GENERAL, groomer.getAddress(), pageable);

        List<EstimateInfo.General.Content> contents = new ArrayList<>();
        for (GroomingEstimate estimate : estimates) {
            User user = userPersist.findByAccountId(estimate.getUserId())
                    .orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

            Pet pet = petPersist.findByPetId(estimate.getPetId())
                    .orElseThrow(() -> new PetException(PetExceptionType.PET_NOT_FOUND));

            contents.add(GroomingEstimateMapper.mapToGeneralContent(estimate, user, pet));
        }
        return EstimateInfo.General.builder()
                .estimates(contents)
                .build();
    }

    @Transactional(readOnly = true)
    public EstimateInfo.Designation findDesignationEstimates(Long accountId, int page, int size) {
        Groomer groomer = groomerPersist.findByAccountId(accountId)
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

        Page<GroomingEstimate> estimates = groomingEstimatePersist.findByStatusAndProposalAndGroomerId(EstimateStatus.NEW, Proposal.DESIGNATION, groomer.getGroomerId(), pageable);

        List<EstimateInfo.Designation.Content> contents = new ArrayList<>();
        for (GroomingEstimate estimate : estimates) {
            User user = userPersist.findByAccountId(estimate.getUserId())
                    .orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

            Pet pet = petPersist.findByPetId(estimate.getPetId())
                    .orElseThrow(() -> new PetException(PetExceptionType.PET_NOT_FOUND));

            contents.add(GroomingEstimateMapper.mapToDesignationContent(estimate, user, pet));
        }
        return EstimateInfo.Designation.builder()
                .estimates(contents)
                .build();
    }

    @Transactional(readOnly = true)
    public EstimateDetail getEstimateDetail(Long estimateId) {
        GroomingEstimate groomingEstimate = groomingEstimatePersist.findByEstimateId(estimateId)
                .orElseThrow(() -> new GroomingEstimateException(GroomingEstimateExceptionType.GROOMING_ESTIMATE_NOT_FOUND));

        User user = userPersist.findByAccountId(groomingEstimate.getUserId())
                .orElseThrow(() -> new UserException(UserExceptionType.USER_NOT_FOUND));

        Pet pet = petPersist.findByPetId(groomingEstimate.getPetId())
                .orElseThrow(() -> new PetException(PetExceptionType.PET_NOT_FOUND));

        return GroomingEstimateMapper.mapToEstimateDetail(groomingEstimate, user, pet);
    }

    @Transactional
    public EstimateResp createPendingEstimate(CreatePendingEstimateReq request, Long accountId) {
        GroomingEstimate.validateOverallOpinion(request.getOverallOpinion());

        GroomingEstimate groomingEstimate = groomingEstimatePersist.findByEstimateId(request.getId())
                .orElseThrow(() -> new GroomingEstimateException(GroomingEstimateExceptionType.GROOMING_ESTIMATE_NOT_FOUND));

        Groomer groomer = groomerPersist.findByAccountId(accountId)
                .orElseThrow(() -> new GroomerException(GroomerExceptionType.GROOMER_NOT_FOUND));

        GroomingEstimate pendingEstimate = GroomingEstimateMapper.createPendingEstimate(request, groomer, groomingEstimate);
        groomingEstimatePersist.save(pendingEstimate);

        return EstimateResp.builder()
                .requestResult("대기 미용 견적서 등록 완료")
                .build();
    }
}
