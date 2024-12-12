package ddog.persistence.mysql.adapter;

import ddog.domain.estimate.EstimateStatus;
import ddog.domain.estimate.GroomingEstimate;
import ddog.domain.estimate.Proposal;
import ddog.domain.estimate.port.GroomingEstimatePersist;
import ddog.persistence.mysql.jpa.entity.GroomingEstimateJpaEntity;
import ddog.persistence.mysql.jpa.repository.GroomingEstimateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GroomingEstimateRepository implements GroomingEstimatePersist {

    private final GroomingEstimateJpaRepository groomingEstimateJpaRepository;

    @Override
    public GroomingEstimate save(GroomingEstimate groomingEstimate) {
        return groomingEstimateJpaRepository.save(GroomingEstimateJpaEntity.from(groomingEstimate))
                .toModel();
    }

    @Override
    public Optional<GroomingEstimate> findByEstimateId(Long estimateId) {
        return groomingEstimateJpaRepository.findByEstimateId(estimateId)
                .map(GroomingEstimateJpaEntity::toModel);
    }

    @Override
    public void updateStatusWithParentId(EstimateStatus estimateStatus, Long parentId) {
        groomingEstimateJpaRepository.updateParentEstimateByParentId(estimateStatus, parentId);
        groomingEstimateJpaRepository.updateStatusByParentId(estimateStatus, parentId);
    }

    public Page<GroomingEstimate> findByPetIdAndStatusAndProposal(Long petId, EstimateStatus status, Proposal proposal, Pageable pageable) {
        return groomingEstimateJpaRepository.findByPetIdAndStatusAndProposal(petId, status, proposal, pageable)
                .map(GroomingEstimateJpaEntity::toModel);
    }

    @Override
    public Optional<GroomingEstimate> findByEstimateStatusAndProposalAndPetId(EstimateStatus estimateStatus, Proposal proposal, Long petId) {
        return groomingEstimateJpaRepository.findTopByStatusAndProposalAndPetId(estimateStatus, proposal, petId)
                .map(GroomingEstimateJpaEntity::toModel);
    }

    public Page<GroomingEstimate> findByStatusAndProposalAndAddress(EstimateStatus status, Proposal proposal, String address, Pageable pageable) {
        return groomingEstimateJpaRepository.findByStatusAndProposalAndAddress(status, proposal, address, pageable)
                .map(GroomingEstimateJpaEntity::toModel);
    }

    @Override
    public Page<GroomingEstimate> findByStatusAndProposalAndGroomerId(EstimateStatus status, Proposal proposal, Long groomerId, Pageable pageable) {
        return groomingEstimateJpaRepository.findByStatusAndProposalAndGroomerId(status, proposal, groomerId, pageable)
                .map(GroomingEstimateJpaEntity::toModel);
    }
}
