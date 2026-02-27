# Design: User Accounts

## Domain Model

### Entities
- **User** — Core user entity (id, email, username, displayName, bio, profilePictureUrl, location, phone, phoneVerified, passwordHash, createdAt, updatedAt, deletedAt)
- **UserPrivacySettings** — Privacy preferences (profileVisibility, postsVisibility, locationVisibility, phoneVisibility)
- **UserSecuritySettings** — Security preferences (twoFactorEnabled)
- **UserDevice** — Tracks login devices (deviceName, ipAddress, userAgent, lastLoginAt)
- **UserFollow** — Follow relationships (followerId, followingId)
- **UserMute** — Mute relationships (muterId, mutedId)
- **UserBlock** — Block relationships (blockerId, blockedId)
- **UserReport** — Reports (reporterId, reportedId, reason, details)

## Backend Architecture (Layered)

### Controller Layer
- `AuthController` — handles auth endpoints
- `UserController` — handles profile and settings endpoints
- `UserSocialController` — handles follow/mute/block/report endpoints

### Service Layer
- `AuthService` — signup, login, logout, token refresh, password reset
- `UserService` — profile CRUD, settings, account deletion
- `UserSocialService` — follow/unfollow, mute/unmute, block/unblock, report

### Repository Layer (Spring Data JPA)
- `UserRepository`
- `UserPrivacySettingsRepository`
- `UserSecuritySettingsRepository`
- `UserDeviceRepository`
- `UserFollowRepository`
- `UserMuteRepository`
- `UserBlockRepository`
- `UserReportRepository`

### Security
- JWT authentication filter
- BCrypt password hashing
- Role-based access (USER role for MVP)

### DTOs
- Request/response DTOs for each endpoint
- Validation annotations on request DTOs

## Database Schema

### Tables
- `users` — core user data
- `user_privacy_settings` — one-to-one with users
- `user_security_settings` — one-to-one with users
- `user_devices` — one-to-many with users
- `user_follows` — many-to-many (follower_id, following_id), unique constraint
- `user_mutes` — many-to-many (muter_id, muted_id), unique constraint
- `user_blocks` — many-to-many (blocker_id, blocked_id), unique constraint
- `user_reports` — many-to-one reporter, many-to-one reported
