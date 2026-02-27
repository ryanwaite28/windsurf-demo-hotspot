package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadSave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreadSaveRepository extends JpaRepository<ThreadSave, UUID> {
    Optional<ThreadSave> findByThreadIdAndUserId(UUID threadId, UUID userId);
    boolean existsByThreadIdAndUserId(UUID threadId, UUID userId);
    Page<ThreadSave> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
