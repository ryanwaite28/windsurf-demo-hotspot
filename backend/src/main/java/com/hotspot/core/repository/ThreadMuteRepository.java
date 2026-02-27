package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadMute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreadMuteRepository extends JpaRepository<ThreadMute, UUID> {
    Optional<ThreadMute> findByThreadIdAndUserId(UUID threadId, UUID userId);
    boolean existsByThreadIdAndUserId(UUID threadId, UUID userId);
}
