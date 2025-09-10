package com.example.SmartFinanceManager.Service;

import com.example.SmartFinanceManager.Dto.ProfileDto;
import com.example.SmartFinanceManager.Model.Profile;
import com.example.SmartFinanceManager.Repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    public ProfileDto registerProfile(ProfileDto profileDto){
        Profile newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        // Send Activation Email
        String activationLink = "http://localhost:8080/activate?token="+ newProfile.getActivationToken();
        String subject = "Activate your Smart Finance Manager Account";
        String body = "Click to activate your Account: " + activationLink;
        emailService.sendMail(newProfile.getEmail(), subject, body);

        return toDto(newProfile);
    }

    public Profile toEntity(ProfileDto profileDto){
        return Profile.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(profileDto.getPassword())
                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }
    public ProfileDto toDto(Profile profile){
        return ProfileDto.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .password(profile.getPassword())
                .profileImageUrl(profile.getProfileImageUrl())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }

}
