package ddog.persistence.mysql.port;

import ddog.domain.estimate.CareEstimate;

import java.util.List;

public interface CareEstimatePersist {
    CareEstimate save(CareEstimate careEstimate);
    CareEstimate getByEstimateId(Long careEstimateId);
    List<CareEstimate> findCareEstimatesByPetId(Long petId);
    List<CareEstimate> findGeneralCareEstimates(String address);
    List<CareEstimate> findDesignationCareEstimates(Long vetId);
}
