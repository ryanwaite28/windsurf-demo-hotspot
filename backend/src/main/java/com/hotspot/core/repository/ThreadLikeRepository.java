package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreadLikeRepository extends JpaRepository<ThreadLike, UUID> {
    Optional<ThreadLike> findByThreadIdAndUserId(UUID threadId, UUID userId);
    boolean existsByThreadIdAndUserId(UUID threadId, UUID userId);
    long countByThreadId(UUID threadId);
}
