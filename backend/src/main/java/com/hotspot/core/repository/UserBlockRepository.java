package com.hotspot.core.repository;

import com.hotspot.core.entity.UserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, UUID> {
    Optional<UserBlock> findByBlockerIdAndBlockedId(UUID blockerId, UUID blockedId);
    boolean existsByBlockerIdAndBlockedId(UUID blockerId, UUID blockedId);
}
