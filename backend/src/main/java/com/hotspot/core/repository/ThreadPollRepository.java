package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadPoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreadPollRepository extends JpaRepository<ThreadPoll, UUID> {
    Optional<ThreadPoll> findByThreadId(UUID threadId);
}
