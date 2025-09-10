package com.example.SmartFinanceManager.Controller;

import com.example.SmartFinanceManager.Dto.ProfileDto;
import com.example.SmartFinanceManager.Service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registerProfile = profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated = profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.ok("Profile Activated Sucesfully");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation Token is not found or already used");
        }
    }

}
