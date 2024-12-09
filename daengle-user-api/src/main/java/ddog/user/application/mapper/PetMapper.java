package ddog.user.application.mapper;

import ddog.domain.pet.Pet;
import ddog.user.presentation.account.dto.AddPetInfo;
import ddog.user.presentation.account.dto.SignUpWithPet;
import ddog.user.presentation.account.dto.ModifyPetInfo;

public class PetMapper {

    public static Pet toJoinPetInfo(Long accountId, SignUpWithPet request) {
        return Pet.builder()
                .accountId(accountId)
                .petName(request.getPetName())
                .petGender(request.getPetGender())
                .petImage("")
                .petWeight(request.getPetWeight())
                .petBirth(request.getPetBirth())
                .isNeutered(request.getIsNeutered())
                .breed(request.getBreed())
                .build();
    }

    public static Pet create(Long accountId, AddPetInfo request) {
        return Pet.builder()
                .accountId(accountId)
                .petGender(request.getGender())
                .petName(request.getName())
                .petImage(request.getImage())
                .petSignificant(request.getSignificant())
                .petBirth(request.getBirth())
                .petWeight(request.getWeight())
                .breed(request.getBreed())
                .isNeutered(request.getIsNeutered())
                .groomingExperience(request.getGroomingExperience())
                .isBite(request.getIsBite())
                .dislikeParts(request.getDislikeParts())
                .significantTags(request.getSignificantTags())
                .build();
    }

    public static Pet withModifyPetInfo(ModifyPetInfo request, Long accountId) {
        return Pet.builder()
                .petId(request.getId())
                .accountId(accountId)
                .petImage(request.getImage())
                .petName(request.getName())
                .petBirth(request.getBirth())
                .petGender(request.getGender())
                .breed(request.getBreed())
                .isNeutered(request.getIsNeutered())
                .petWeight(request.getWeight())
                .groomingExperience(request.getGroomingExperience())
                .isBite(request.getIsBite())
                .dislikeParts(request.getDislikeParts())
                .significantTags(request.getSignificantTags())
                .petSignificant(request.getSignificant())
                .build();
    }

}
