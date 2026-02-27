# Design: Social Zone

## Domain Model

### Entities
- **Thread** — Core post entity (id, authorId, content, threadType [ORIGINAL, REPOST, QUOTE], parentThreadId [for reposts/quotes], isPinned, createdAt, updatedAt, deletedAt)
- **ThreadMedia** — Media attachments (id, threadId, mediaType [IMAGE, VIDEO, AUDIO, VOICE_NOTE], url, order)
- **ThreadPoll** — Poll on a thread (id, threadId, question, expiresAt)
- **ThreadPollOption** — Poll options (id, pollId, optionText, order)
- **ThreadPollVote** — Poll votes (id, pollOptionId, userId)
- **ThreadLike** — Like on a thread (id, threadId, userId)
- **ThreadReaction** — Reaction on a thread (id, threadId, userId, reactionType [HEART, LAUGH, FIRE, SAD, ANGRY])
- **ThreadComment** — Comment on a thread (id, threadId, authorId, content, createdAt, updatedAt, deletedAt)
- **ThreadSave** — Bookmarked thread (id, threadId, userId)
- **ThreadReport** — Report on a thread (id, threadId, reporterId, reason, details)
- **ThreadMute** — Muted thread (id, threadId, userId)
- **CommentReport** — Report on a comment (id, commentId, reporterId, reason, details)
- **CommentMute** — Muted comment (id, commentId, userId)

## Backend Architecture (Layered)

### Controller Layer
- `ThreadController` — thread CRUD and interactions
- `CommentController` — comment CRUD
- `FeedController` — feed retrieval

### Service Layer
- `ThreadService` — thread CRUD, repost, quote, pin, edit validation (10 min window)
- `ThreadInteractionService` — like, react, save, report, mute, share
- `CommentService` — comment CRUD, edit validation (10 min window), report, mute
- `FeedService` — feed aggregation from followed users

### Repository Layer (Spring Data JPA)
- `ThreadRepository`
- `ThreadMediaRepository`
- `ThreadPollRepository`
- `ThreadPollOptionRepository`
- `ThreadPollVoteRepository`
- `ThreadLikeRepository`
- `ThreadReactionRepository`
- `ThreadCommentRepository`
- `ThreadSaveRepository`
- `ThreadReportRepository`
- `ThreadMuteRepository`
- `CommentReportRepository`
- `CommentMuteRepository`

## Database Schema

### Tables
- `threads` — core thread data with soft delete
- `thread_media` — media attachments, ordered
- `thread_polls` — one-to-one with threads
- `thread_poll_options` — one-to-many with polls
- `thread_poll_votes` — unique constraint (user + poll_option)
- `thread_likes` — unique constraint (user + thread)
- `thread_reactions` — unique constraint (user + thread)
- `thread_comments` — soft delete
- `thread_saves` — unique constraint (user + thread)
- `thread_reports` — many-to-one thread, many-to-one reporter
- `thread_mutes` — unique constraint (user + thread)
- `comment_reports` — many-to-one comment, many-to-one reporter
- `comment_mutes` — unique constraint (user + comment)

## Business Rules
- Threads can only be edited within 10 minutes of creation
- Comments can only be edited within 10 minutes of creation
- A user can only have one reaction type per thread (changing reaction replaces previous)
- Reposts reference the original thread; quotes reference and add content
- Deleted threads are soft-deleted (set deletedAt)
- Feed returns threads from followed users, sorted by recency, paginated
