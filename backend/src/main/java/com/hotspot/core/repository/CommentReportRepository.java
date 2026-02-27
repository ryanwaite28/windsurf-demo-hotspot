package com.hotspot.core.repository;

import com.hotspot.core.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, UUID> {
    boolean existsByCommentIdAndReporterId(UUID commentId, UUID reporterId);
}
