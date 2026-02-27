package com.hotspot.core.repository;

import com.hotspot.core.entity.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, UUID> {
    Optional<UserFollow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);
    Page<UserFollow> findByFollowingId(UUID followingId, Pageable pageable);
    Page<UserFollow> findByFollowerId(UUID followerId, Pageable pageable);
    long countByFollowingId(UUID followingId);
    long countByFollowerId(UUID followerId);
}
