package com.hotspot.core.repository;

import com.hotspot.core.entity.UserMute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMuteRepository extends JpaRepository<UserMute, UUID> {
    Optional<UserMute> findByMuterIdAndMutedId(UUID muterId, UUID mutedId);
    boolean existsByMuterIdAndMutedId(UUID muterId, UUID mutedId);
}
