package com.example.SmartFinanceManager.Service;

import com.example.SmartFinanceManager.Dto.AuthDto;
import com.example.SmartFinanceManager.Dto.ProfileDto;
import com.example.SmartFinanceManager.Model.Profile;
import com.example.SmartFinanceManager.Repository.ProfileRepository;
import com.example.SmartFinanceManager.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Value("${app.activation.url}")
    private String activationUrl;

    public ProfileDto registerProfile(ProfileDto profileDto) {
        Profile newProfile = toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile = profileRepository.save(newProfile);

        // Send Activation Email
        String activationLink = activationUrl+"/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your Smart Finance Manager Account";
        String body = "Click to activate your Account: " + activationLink;
        emailService.sendMail(newProfile.getEmail(), subject, body);

        return toDto(newProfile);
    }

    public Profile toEntity(ProfileDto profileDto) {
        return Profile.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }

    public ProfileDto toDto(Profile profile) {
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

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email)
                .map(Profile::getIsActive)
                .orElse(false);
    }

    public Profile getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + authentication.getName()));
    }

    public ProfileDto getPublicProfile(String email) {
        Profile currentUser = null;
        if (email == null) {
            currentUser = getCurrentProfile();
        } else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));
        }
        return ProfileDto.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));

            // Generate Jwt Token
            String token = jwtUtils.generateToken(authDto.getEmail());

            return Map.of(
                    "token", token,
                    "user", getPublicProfile(authDto.getEmail())
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid Email or Password.");
        }
    }
}
