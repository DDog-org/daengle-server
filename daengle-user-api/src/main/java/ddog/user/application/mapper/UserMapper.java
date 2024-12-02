package ddog.user.application.mapper;

import ddog.domain.pet.Pet;
import ddog.domain.user.User;
import ddog.user.presentation.account.dto.*;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static User createWithPet(Long accountId, SignUpWithPet request, Pet pet) {
        List<Pet> pets = new ArrayList<>();
        pets.add(pet);
        return User.builder()
                .accountId(accountId)
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .nickname(request.getNickname())
                .address(request.getAddress())
                .email(request.getEmail())
                .pets(pets)
                .build();
    }

    public static User createWithoutPet(Long accountId, SignUpWithoutPet request) {
        return User.builder()
                .accountId(accountId)
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .nickname(request.getNickname())
                .address(request.getAddress())
                .email(request.getEmail())
                .pets(new ArrayList<>())
                .build();
    }

    public static ProfileInfo.ModifyPage toUserProfileInfo(User user) {
        return ProfileInfo.ModifyPage.builder()
                .image(user.getUserImage())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .build();
    }

    public static PetInfo toPetInfo(User user) {
        List<PetInfo.Detail> petDetails = new ArrayList<>();
        for (Pet pet : user.getPets()) {
            petDetails.add(PetInfo.Detail.builder()
                    .id(pet.getPetId())
                    .image(pet.getPetImage())
                    .name(pet.getPetName())
                    .birth(pet.getPetBirth())
                    .gender(pet.getPetGender())
                    .breed(pet.getBreed())
                    .isNeutered(pet.getIsNeutered())
                    .weight(pet.getPetWeight())
                    .groomingExperience(pet.getGroomingExperience())
                    .isBite(pet.getIsBite())
                    .dislikeParts(pet.getDislikeParts().split(","))
                    .significant(pet.getPetSignificant())
                    .build());
        }
        return PetInfo.builder()
                .petDetails(petDetails)
                .build();
    }

    public static UserInfo toUserInfo(User user) {
        List<UserInfo.PetInfo> petInfos = new ArrayList<>();
        for (Pet pet : user.getPets()) {
            petInfos.add(UserInfo.PetInfo.builder()
                    .id(pet.getPetId())
                    .image(pet.getPetImage())
                    .name(pet.getPetName())
                    .significant(pet.getPetSignificant())
                    .birth(pet.getPetBirth())
                    .weight(pet.getPetWeight())
                    .build());
        }

        return UserInfo.builder()
                .nickname(user.getNickname())
                .userImage(user.getUserImage())
                .address(user.getAddress())
                .petInfos(petInfos)
                .build();
    }

}
