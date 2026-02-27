package com.hotspot.core.repository;

import com.hotspot.core.entity.Thread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, UUID> {

    Optional<Thread> findByIdAndDeletedAtIsNull(UUID id);

    Page<Thread> findByAuthorIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID authorId, Pageable pageable);

    @Query("SELECT t FROM Thread t WHERE t.deletedAt IS NULL AND t.author.id IN :authorIds ORDER BY t.createdAt DESC")
    Page<Thread> findByAuthorIdInAndDeletedAtIsNull(@Param("authorIds") List<UUID> authorIds, Pageable pageable);

    Page<Thread> findByAuthorIdAndPinnedTrueAndDeletedAtIsNull(UUID authorId, Pageable pageable);
}
