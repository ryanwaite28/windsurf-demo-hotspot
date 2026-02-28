export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface PagedData<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  user: UserInfo;
}

export interface UserInfo {
  id: string;
  email: string;
  username: string;
  displayName: string;
}

export interface UserProfile {
  id: string;
  email: string;
  username: string;
  displayName: string;
  bio: string | null;
  profilePictureUrl: string | null;
  location: string | null;
  phone: string | null;
  followersCount: number;
  followingCount: number;
  createdAt: string;
}

export interface PrivacySettings {
  profileVisibility: VisibilitySetting;
  postsVisibility: VisibilitySetting;
  locationVisibility: VisibilitySetting;
  phoneVisibility: VisibilitySetting;
}

export interface SecuritySettings {
  twoFactorEnabled: boolean;
}

export type VisibilitySetting = 'PUBLIC' | 'FOLLOWERS' | 'NOBODY';

export interface SignupRequest {
  email: string;
  displayName: string;
  username: string;
  password: string;
  confirmPassword: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface UpdateProfileRequest {
  displayName?: string;
  username?: string;
  bio?: string;
  location?: string;
}

export interface UpdatePrivacyRequest {
  profileVisibility?: VisibilitySetting;
  postsVisibility?: VisibilitySetting;
  locationVisibility?: VisibilitySetting;
  phoneVisibility?: VisibilitySetting;
}

export interface UpdateSecurityRequest {
  twoFactorEnabled?: boolean;
}

export interface PasswordResetRequest {
  email: string;
}

export interface FollowUser {
  id: string;
  username: string;
  displayName: string;
  profilePictureUrl: string | null;
}
