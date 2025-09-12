package com.example.SmartFinanceManager.Security;

import com.example.SmartFinanceManager.Model.Profile;
import com.example.SmartFinanceManager.Repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;


    // This method is responsible for loading data from database
    // Returns the userdetails

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Profile  existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Profile not found with this email : " + email));
        return User.builder()
                .username(existingProfile.getEmail())
                .password(existingProfile.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }

}
