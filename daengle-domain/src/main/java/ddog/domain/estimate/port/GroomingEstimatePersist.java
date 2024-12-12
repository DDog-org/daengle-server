package ddog.domain.estimate.port;

import ddog.domain.estimate.EstimateStatus;
import ddog.domain.estimate.GroomingEstimate;
import ddog.domain.estimate.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GroomingEstimatePersist {
    GroomingEstimate save(GroomingEstimate groomingEstimate);

    Optional<GroomingEstimate> findByEstimateId(Long groomingEstimateId);

    void updateStatusWithParentId(EstimateStatus estimateStatus, Long parentId);

    Page<GroomingEstimate> findByPetIdAndStatusAndProposal(Long petId, EstimateStatus status, Proposal proposal, Pageable pageable);

    Optional<GroomingEstimate> findByEstimateStatusAndProposalAndPetId(EstimateStatus estimateStatus, Proposal proposal, Long petId);

    Page<GroomingEstimate> findByStatusAndProposalAndAddress(EstimateStatus status, Proposal proposal, String address, Pageable pageable);

    Page<GroomingEstimate> findByStatusAndProposalAndGroomerId(EstimateStatus status, Proposal proposal, Long groomerId, Pageable pageable);
}
