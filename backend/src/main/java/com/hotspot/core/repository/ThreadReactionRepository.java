package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadReaction;
import com.hotspot.core.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreadReactionRepository extends JpaRepository<ThreadReaction, UUID> {
    Optional<ThreadReaction> findByThreadIdAndUserId(UUID threadId, UUID userId);
    boolean existsByThreadIdAndUserId(UUID threadId, UUID userId);
    long countByThreadIdAndReactionType(UUID threadId, ReactionType reactionType);
    long countByThreadId(UUID threadId);
}
