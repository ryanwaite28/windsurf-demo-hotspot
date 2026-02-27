package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ThreadReportRepository extends JpaRepository<ThreadReport, UUID> {
    boolean existsByThreadIdAndReporterId(UUID threadId, UUID reporterId);
}
