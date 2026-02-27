package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThreadMediaRepository extends JpaRepository<ThreadMedia, UUID> {
    List<ThreadMedia> findByThreadIdOrderByDisplayOrder(UUID threadId);
    void deleteByThreadId(UUID threadId);
}
