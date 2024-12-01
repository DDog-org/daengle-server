package ddog.persistence.jpa.entity;

import ddog.domain.estimate.EstimateStatus;
import ddog.domain.estimate.GroomingEstimate;
import ddog.domain.estimate.Proposal;
import ddog.domain.pet.Weight;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "GroomingEstimates")
public class GroomingEstimateJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groomingEstimateId;
    private LocalDateTime reservedDate;
    private String desiredStyle;
    private String requirements;
    @Enumerated(EnumType.STRING)
    private Proposal proposal;
    @Enumerated(EnumType.STRING)
    private EstimateStatus status;
    private LocalDateTime createdAt;

    private Long userId;
    private String userImage;
    private String nickname;
    private String address;

    private Long petId;
    private String petImage;
    private String petName;
    private int petBirth;
    @Enumerated(EnumType.STRING)
    private Weight petWeight;
    private String petSignificant;

    private Long groomerId;
    private int daengleMeter;
    private String overallOpinion;
    private String groomerImage;
    private String groomerName;
    private String shopName;
    private String groomerIntroduction;

    public GroomingEstimate toModel() {
        return GroomingEstimate.builder()
                .groomingEstimateId(groomingEstimateId)
                .reservedDate(reservedDate)
                .desiredStyle(desiredStyle)
                .requirements(requirements)
                .proposal(proposal)
                .status(status)
                .createdAt(createdAt)
                .userId(userId)
                .userImage(userImage)
                .nickname(nickname)
                .address(address)
                .petId(petId)
                .petImage(petImage)
                .petName(petName)
                .petBirth(petBirth)
                .petWeight(petWeight)
                .petSignificant(petSignificant)
                .groomerId(groomerId)
                .daengleMeter(daengleMeter)
                .overallOpinion(overallOpinion)
                .groomerImage(groomerImage)
                .groomerName(groomerName)
                .shopName(shopName)
                .groomerIntroduction(groomerIntroduction)
                .build();
    }

    public static GroomingEstimateJpaEntity from(GroomingEstimate groomingEstimate) {
        return GroomingEstimateJpaEntity.builder()
                .groomingEstimateId(groomingEstimate.getGroomingEstimateId())
                .reservedDate(groomingEstimate.getReservedDate())
                .desiredStyle(groomingEstimate.getDesiredStyle())
                .requirements(groomingEstimate.getRequirements())
                .proposal(groomingEstimate.getProposal())
                .status(groomingEstimate.getStatus())
                .createdAt(groomingEstimate.getCreatedAt())
                .userId(groomingEstimate.getUserId())
                .userImage(groomingEstimate.getUserImage())
                .nickname(groomingEstimate.getNickname())
                .address(groomingEstimate.getAddress())
                .petId(groomingEstimate.getPetId())
                .petImage(groomingEstimate.getPetImage())
                .petName(groomingEstimate.getPetName())
                .petBirth(groomingEstimate.getPetBirth())
                .petWeight(groomingEstimate.getPetWeight())
                .petSignificant(groomingEstimate.getPetSignificant())
                .groomerId(groomingEstimate.getGroomerId())
                .daengleMeter(groomingEstimate.getDaengleMeter())
                .overallOpinion(groomingEstimate.getOverallOpinion())
                .groomerImage(groomingEstimate.getGroomerImage())
                .groomerName(groomingEstimate.getGroomerName())
                .shopName(groomingEstimate.getShopName())
                .groomerIntroduction(groomingEstimate.getGroomerIntroduction())
                .build();
    }
}
