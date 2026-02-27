package com.hotspot.core.repository;

import com.hotspot.core.entity.ThreadPollOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThreadPollOptionRepository extends JpaRepository<ThreadPollOption, UUID> {
    List<ThreadPollOption> findByPollIdOrderByDisplayOrder(UUID pollId);
}
