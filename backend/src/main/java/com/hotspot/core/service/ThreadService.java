package com.hotspot.core.service;

import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ForbiddenException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.social.*;
import com.hotspot.core.entity.*;
import com.hotspot.core.entity.Thread;
import com.hotspot.core.enums.MediaType;
import com.hotspot.core.enums.ThreadType;
import com.hotspot.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThreadService {

    private static final Duration EDIT_WINDOW = Duration.ofMinutes(10);

    private final ThreadRepository threadRepository;
    private final ThreadMediaRepository mediaRepository;
    private final ThreadPollRepository pollRepository;
    private final ThreadPollOptionRepository pollOptionRepository;
    private final ThreadPollVoteRepository pollVoteRepository;
    private final ThreadLikeRepository likeRepository;
    private final ThreadReactionRepository reactionRepository;
    private final ThreadCommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ThreadResponse createThread(UUID authorId, CreateThreadRequest request) {
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));

        if (request.getContent() == null && (request.getMedia() == null || request.getMedia().isEmpty())
                && request.getPoll() == null) {
            throw new BadRequestException("Thread must have content, media, or a poll");
        }

        Thread thread = Thread.builder()
            .author(author)
            .content(request.getContent())
            .threadType(ThreadType.ORIGINAL)
            .build();
        thread = threadRepository.save(thread);

        if (request.getMedia() != null && !request.getMedia().isEmpty()) {
            saveMedia(thread, request.getMedia());
        }

        if (request.getPoll() != null) {
            savePoll(thread, request.getPoll());
        }

        return mapToResponse(thread);
    }

    public ThreadResponse getThread(UUID threadId) {
        Thread thread = threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));
        return mapToResponse(thread);
    }

    @Transactional
    public ThreadResponse editThread(UUID userId, UUID threadId, EditThreadRequest request) {
        Thread thread = threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));

        if (!thread.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own threads");
        }

        if (Duration.between(thread.getCreatedAt(), Instant.now()).compareTo(EDIT_WINDOW) > 0) {
            throw new BadRequestException("Threads can only be edited within 10 minutes of creation");
        }

        thread.setContent(request.getContent());
        thread = threadRepository.save(thread);
        return mapToResponse(thread);
    }

    @Transactional
    public void deleteThread(UUID userId, UUID threadId) {
        Thread thread = threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));

        if (!thread.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own threads");
        }

        thread.setDeletedAt(Instant.now());
        threadRepository.save(thread);
    }

    @Transactional
    public ThreadResponse repostThread(UUID userId, UUID threadId) {
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Thread parentThread = threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));

        Thread repost = Thread.builder()
            .author(author)
            .threadType(ThreadType.REPOST)
            .parentThread(parentThread)
            .build();
        repost = threadRepository.save(repost);
        return mapToResponse(repost);
    }

    @Transactional
    public ThreadResponse quoteThread(UUID userId, UUID threadId, QuoteThreadRequest request) {
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Thread parentThread = threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));

        Thread quote = Thread.builder()
            .author(author)
            .content(request.getContent())
            .threadType(ThreadType.QUOTE)
            .parentThread(parentThread)
            .build();
        quote = threadRepository.save(quote);
        return mapToResponse(quote);
    }

    @Transactional
    public ThreadResponse togglePin(UUID userId, UUID threadId) {
        Thread thread = threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));

        if (!thread.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You can only pin your own threads");
        }

        thread.setPinned(!thread.isPinned());
        thread = threadRepository.save(thread);
        return mapToResponse(thread);
    }

    public Page<ThreadResponse> getUserThreads(UUID userId, Pageable pageable) {
        Page<Thread> threads = threadRepository.findByAuthorIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId, pageable);
        return threads.map(this::mapToResponse);
    }

    private void saveMedia(Thread thread, List<CreateThreadRequest.MediaItem> mediaItems) {
        for (int i = 0; i < mediaItems.size(); i++) {
            CreateThreadRequest.MediaItem item = mediaItems.get(i);
            ThreadMedia media = ThreadMedia.builder()
                .thread(thread)
                .mediaType(MediaType.valueOf(item.getMediaType().toUpperCase()))
                .url(item.getUrl())
                .displayOrder(i)
                .build();
            mediaRepository.save(media);
        }
    }

    private void savePoll(Thread thread, CreateThreadRequest.PollRequest pollRequest) {
        ThreadPoll poll = ThreadPoll.builder()
            .thread(thread)
            .question(pollRequest.getQuestion())
            .expiresAt(pollRequest.getExpiresInHours() != null
                ? Instant.now().plus(Duration.ofHours(pollRequest.getExpiresInHours()))
                : null)
            .build();
        poll = pollRepository.save(poll);

        for (int i = 0; i < pollRequest.getOptions().size(); i++) {
            ThreadPollOption option = ThreadPollOption.builder()
                .poll(poll)
                .optionText(pollRequest.getOptions().get(i))
                .displayOrder(i)
                .build();
            pollOptionRepository.save(option);
        }
    }

    public ThreadResponse mapToResponse(Thread thread) {
        List<ThreadMedia> mediaList = mediaRepository.findByThreadIdOrderByDisplayOrder(thread.getId());
        List<ThreadResponse.MediaResponse> mediaResponses = mediaList.stream()
            .map(m -> ThreadResponse.MediaResponse.builder()
                .id(m.getId().toString())
                .mediaType(m.getMediaType().name())
                .url(m.getUrl())
                .displayOrder(m.getDisplayOrder())
                .build())
            .toList();

        ThreadResponse.PollResponse pollResponse = null;
        ThreadPoll poll = pollRepository.findByThreadId(thread.getId()).orElse(null);
        if (poll != null) {
            List<ThreadPollOption> options = pollOptionRepository.findByPollIdOrderByDisplayOrder(poll.getId());
            List<ThreadResponse.PollOptionResponse> optionResponses = options.stream()
                .map(o -> ThreadResponse.PollOptionResponse.builder()
                    .id(o.getId().toString())
                    .optionText(o.getOptionText())
                    .voteCount(pollVoteRepository.countByPollOptionId(o.getId()))
                    .displayOrder(o.getDisplayOrder())
                    .build())
                .toList();

            pollResponse = ThreadResponse.PollResponse.builder()
                .id(poll.getId().toString())
                .question(poll.getQuestion())
                .options(optionResponses)
                .expiresAt(poll.getExpiresAt())
                .build();
        }

        long likesCount = likeRepository.countByThreadId(thread.getId());
        long reactionsCount = reactionRepository.countByThreadId(thread.getId());
        long commentsCount = commentRepository.countByThreadIdAndDeletedAtIsNull(thread.getId());

        User author = thread.getAuthor();

        return ThreadResponse.builder()
            .id(thread.getId().toString())
            .author(ThreadResponse.AuthorInfo.builder()
                .id(author.getId().toString())
                .username(author.getUsername())
                .displayName(author.getDisplayName())
                .profilePictureUrl(author.getProfilePictureUrl())
                .build())
            .content(thread.getContent())
            .threadType(thread.getThreadType())
            .parentThreadId(thread.getParentThread() != null ? thread.getParentThread().getId().toString() : null)
            .pinned(thread.isPinned())
            .likesCount(likesCount)
            .reactionsCount(reactionsCount)
            .commentsCount(commentsCount)
            .media(mediaResponses)
            .poll(pollResponse)
            .createdAt(thread.getCreatedAt())
            .updatedAt(thread.getUpdatedAt())
            .build();
    }
}
