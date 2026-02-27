package com.hotspot.core.repository;

import com.hotspot.core.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, UUID> {
    boolean existsByReporterIdAndReportedId(UUID reporterId, UUID reportedId);
}
