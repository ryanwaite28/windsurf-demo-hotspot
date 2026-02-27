package com.hotspot.core.repository;

import com.hotspot.core.entity.UserPrivacySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPrivacySettingsRepository extends JpaRepository<UserPrivacySettings, UUID> {
    Optional<UserPrivacySettings> findByUserId(UUID userId);
}
