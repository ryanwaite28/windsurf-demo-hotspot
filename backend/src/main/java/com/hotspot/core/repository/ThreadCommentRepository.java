package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreadCommentRepository extends JpaRepository<ThreadComment, UUID> {
    Page<ThreadComment> findByThreadIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID threadId, Pageable pageable);
    Optional<ThreadComment> findByIdAndDeletedAtIsNull(UUID id);
    long countByThreadIdAndDeletedAtIsNull(UUID threadId);
}
