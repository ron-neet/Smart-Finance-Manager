package com.example.SmartFinanceManager.Repository;

import com.example.SmartFinanceManager.Model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByActivationToken(String activationToken);
}
