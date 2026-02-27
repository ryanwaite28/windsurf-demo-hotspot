package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadPollVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreadPollVoteRepository extends JpaRepository<ThreadPollVote, UUID> {
    Optional<ThreadPollVote> findByPollOptionIdAndUserId(UUID pollOptionId, UUID userId);
    boolean existsByPollOptionIdAndUserId(UUID pollOptionId, UUID userId);
    long countByPollOptionId(UUID pollOptionId);
}
