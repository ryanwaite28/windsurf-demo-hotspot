# Spec: Social Zone

## Overview
The Social Zone is the first zone in HotSpot. It provides Twitter/Threads-style microblogging functionality where users create "threads" (posts), interact with them, and engage with other users' content.

## Functional Requirements

### Threads (Posts)
- Users can create threads (text content, optional media)
- Users can repost threads (share without additional content)
- Users can quote threads (share with additional content)
- Users can edit threads within 10 minutes of creation
- Users can delete threads
- Users can pin threads to their profile
- Users can add media to threads (images, videos, audio)
- Users can add polls to threads
- Users can add voice notes to threads

### Thread Interactions
- Users can like/unlike threads
- Users can react to threads (heart, laugh, fire, sad, angry)
- Users can comment on threads
- Users can share threads (generates a shareable link)
- Users can save/unsave threads (bookmarks)
- Users can report threads
- Users can mute threads (stop notifications)

### Comments
- Users can comment on threads
- Users can edit comments within 10 minutes of creation
- Users can delete comments
- Users can report comments
- Users can mute comments (stop notifications)

### Feed
- Users see a feed of threads from users they follow
- Feed is sorted by recency (reverse chronological) for MVP

## API Endpoints (Backend)

### Threads
- `POST /api/v1/social/threads` — Create a thread
- `GET /api/v1/social/threads/{threadId}` — Get a thread
- `PUT /api/v1/social/threads/{threadId}` — Edit a thread (within 10 min)
- `DELETE /api/v1/social/threads/{threadId}` — Delete a thread
- `POST /api/v1/social/threads/{threadId}/repost` — Repost a thread
- `POST /api/v1/social/threads/{threadId}/quote` — Quote a thread
- `PUT /api/v1/social/threads/{threadId}/pin` — Pin/unpin a thread

### Thread Interactions
- `POST /api/v1/social/threads/{threadId}/like` — Like a thread
- `DELETE /api/v1/social/threads/{threadId}/like` — Unlike a thread
- `POST /api/v1/social/threads/{threadId}/react` — React to a thread
- `DELETE /api/v1/social/threads/{threadId}/react` — Remove reaction
- `POST /api/v1/social/threads/{threadId}/save` — Save/bookmark a thread
- `DELETE /api/v1/social/threads/{threadId}/save` — Unsave a thread
- `POST /api/v1/social/threads/{threadId}/report` — Report a thread
- `POST /api/v1/social/threads/{threadId}/mute` — Mute a thread
- `DELETE /api/v1/social/threads/{threadId}/mute` — Unmute a thread
- `GET /api/v1/social/threads/{threadId}/share-link` — Get shareable link

### Comments
- `POST /api/v1/social/threads/{threadId}/comments` — Create a comment
- `GET /api/v1/social/threads/{threadId}/comments` — List comments on a thread
- `PUT /api/v1/social/comments/{commentId}` — Edit a comment (within 10 min)
- `DELETE /api/v1/social/comments/{commentId}` — Delete a comment
- `POST /api/v1/social/comments/{commentId}/report` — Report a comment
- `POST /api/v1/social/comments/{commentId}/mute` — Mute a comment
- `DELETE /api/v1/social/comments/{commentId}/mute` — Unmute a comment

### Feed
- `GET /api/v1/social/feed` — Get user's feed (paginated)

### User Threads
- `GET /api/v1/social/users/{userId}/threads` — Get user's threads (paginated)
- `GET /api/v1/social/users/{userId}/saved` — Get user's saved threads
