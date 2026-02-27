package com.hotspot.core.repository;

import com.hotspot.core.entity.CommentMute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentMuteRepository extends JpaRepository<CommentMute, UUID> {
    Optional<CommentMute> findByCommentIdAndUserId(UUID commentId, UUID userId);
    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);
}
