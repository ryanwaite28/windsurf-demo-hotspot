package com.hotspot.core.service;

import com.hotspot.common.exception.ConflictException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.social.ReactRequest;
import com.hotspot.core.dto.user.ReportRequest;
import com.hotspot.core.entity.*;
import com.hotspot.core.entity.Thread;
import com.hotspot.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThreadInteractionService {

    private final ThreadRepository threadRepository;
    private final ThreadLikeRepository likeRepository;
    private final ThreadReactionRepository reactionRepository;
    private final ThreadSaveRepository saveRepository;
    private final ThreadReportRepository reportRepository;
    private final ThreadMuteRepository muteRepository;
    private final UserRepository userRepository;

    @Transactional
    public void likeThread(UUID userId, UUID threadId) {
        Thread thread = findThread(threadId);
        User user = findUser(userId);

        if (likeRepository.existsByThreadIdAndUserId(threadId, userId)) {
            throw new ConflictException("Thread already liked");
        }

        ThreadLike like = ThreadLike.builder()
            .thread(thread)
            .user(user)
            .build();
        likeRepository.save(like);
    }

    @Transactional
    public void unlikeThread(UUID userId, UUID threadId) {
        ThreadLike like = likeRepository.findByThreadIdAndUserId(threadId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread not liked"));
        likeRepository.delete(like);
    }

    @Transactional
    public void reactToThread(UUID userId, UUID threadId, ReactRequest request) {
        Thread thread = findThread(threadId);
        User user = findUser(userId);

        ThreadReaction existing = reactionRepository.findByThreadIdAndUserId(threadId, userId).orElse(null);
        if (existing != null) {
            existing.setReactionType(request.getReactionType());
            reactionRepository.save(existing);
        } else {
            ThreadReaction reaction = ThreadReaction.builder()
                .thread(thread)
                .user(user)
                .reactionType(request.getReactionType())
                .build();
            reactionRepository.save(reaction);
        }
    }

    @Transactional
    public void removeReaction(UUID userId, UUID threadId) {
        ThreadReaction reaction = reactionRepository.findByThreadIdAndUserId(threadId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("No reaction found"));
        reactionRepository.delete(reaction);
    }

    @Transactional
    public void saveThread(UUID userId, UUID threadId) {
        Thread thread = findThread(threadId);
        User user = findUser(userId);

        if (saveRepository.existsByThreadIdAndUserId(threadId, userId)) {
            throw new ConflictException("Thread already saved");
        }

        ThreadSave save = ThreadSave.builder()
            .thread(thread)
            .user(user)
            .build();
        saveRepository.save(save);
    }

    @Transactional
    public void unsaveThread(UUID userId, UUID threadId) {
        ThreadSave save = saveRepository.findByThreadIdAndUserId(threadId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread not saved"));
        saveRepository.delete(save);
    }

    @Transactional
    public void reportThread(UUID userId, UUID threadId, ReportRequest request) {
        Thread thread = findThread(threadId);
        User reporter = findUser(userId);

        ThreadReport report = ThreadReport.builder()
            .thread(thread)
            .reporter(reporter)
            .reason(request.getReason())
            .details(request.getDetails())
            .build();
        reportRepository.save(report);
    }

    @Transactional
    public void muteThread(UUID userId, UUID threadId) {
        Thread thread = findThread(threadId);
        User user = findUser(userId);

        if (muteRepository.existsByThreadIdAndUserId(threadId, userId)) {
            throw new ConflictException("Thread already muted");
        }

        ThreadMute mute = ThreadMute.builder()
            .thread(thread)
            .user(user)
            .build();
        muteRepository.save(mute);
    }

    @Transactional
    public void unmuteThread(UUID userId, UUID threadId) {
        ThreadMute mute = muteRepository.findByThreadIdAndUserId(threadId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread not muted"));
        muteRepository.delete(mute);
    }

    private Thread findThread(UUID threadId) {
        return threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
