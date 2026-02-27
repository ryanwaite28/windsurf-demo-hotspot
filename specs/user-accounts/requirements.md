# Spec: User Accounts

## Overview
User account management for HotSpot, covering registration, authentication, profile management, privacy/security settings, and social interactions (follow, mute, block, report).

## Functional Requirements

### Authentication
- Users can sign up with: email, display name, username, password, confirm password
- Users can log in with: email, password
- Users can log out
- Users can reset their password
- JWT-based authentication with access and refresh tokens

### Profile
- Users have profiles with:
  - Email
  - Phone (optional, hidden/visible via settings)
  - Display Name
  - Username (unique)
  - Bio
  - Profile Picture
  - Location (optional, hidden/visible via settings)
- Users can update their profile: display name, username, bio, profile picture, location
- Users can add/change/verify their phone number
- Users can change their email
- Users can delete their account

### Privacy Settings
- Who can see user's profile (public, followers, nobody)
- Who can see user's posts (public, followers, nobody)
- Who can see user's location (public, followers, nobody)
- Who can see user's phone number (public, followers, nobody)

### Security Settings
- 2FA (enable/disable)
- Devices: list of devices that have logged in

### Social Interactions
- Users can follow/unfollow other users
- Users can mute/unmute other users
- Users can block/unblock other users
- Users can report other users

## API Endpoints (Backend)

### Auth
- `POST /api/v1/auth/signup` — Register a new user
- `POST /api/v1/auth/login` — Log in
- `POST /api/v1/auth/logout` — Log out
- `POST /api/v1/auth/refresh` — Refresh access token
- `POST /api/v1/auth/password-reset/request` — Request password reset
- `POST /api/v1/auth/password-reset/confirm` — Confirm password reset

### Profile
- `GET /api/v1/users/{username}` — Get user profile
- `PUT /api/v1/users/me` — Update own profile
- `DELETE /api/v1/users/me` — Delete own account
- `PUT /api/v1/users/me/email` — Change email
- `PUT /api/v1/users/me/phone` — Add/change phone number
- `POST /api/v1/users/me/phone/verify` — Verify phone number
- `GET /api/v1/users/me/settings` — Get settings
- `PUT /api/v1/users/me/settings/privacy` — Update privacy settings
- `PUT /api/v1/users/me/settings/security` — Update security settings

### Social Interactions
- `POST /api/v1/users/{userId}/follow` — Follow user
- `DELETE /api/v1/users/{userId}/follow` — Unfollow user
- `POST /api/v1/users/{userId}/mute` — Mute user
- `DELETE /api/v1/users/{userId}/mute` — Unmute user
- `POST /api/v1/users/{userId}/block` — Block user
- `DELETE /api/v1/users/{userId}/block` — Unblock user
- `POST /api/v1/users/{userId}/report` — Report user
- `GET /api/v1/users/{userId}/followers` — Get followers
- `GET /api/v1/users/{userId}/following` — Get following
