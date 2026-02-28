export type ThreadType = 'ORIGINAL' | 'REPOST' | 'QUOTE';
export type MediaType = 'IMAGE' | 'VIDEO' | 'AUDIO' | 'VOICE_NOTE';
export type ReactionType = 'HEART' | 'LAUGH' | 'FIRE' | 'SAD' | 'ANGRY';

export interface Thread {
  id: string;
  authorId: string;
  authorUsername: string;
  authorDisplayName: string;
  authorProfilePictureUrl: string | null;
  content: string;
  threadType: ThreadType;
  parentThreadId: string | null;
  parentThread: Thread | null;
  isPinned: boolean;
  media: ThreadMedia[];
  poll: ThreadPoll | null;
  likesCount: number;
  commentsCount: number;
  repostsCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ThreadMedia {
  id: string;
  mediaType: MediaType;
  url: string;
  order: number;
}

export interface ThreadPoll {
  id: string;
  question: string;
  options: ThreadPollOption[];
  expiresAt: string;
  totalVotes: number;
}

export interface ThreadPollOption {
  id: string;
  optionText: string;
  order: number;
  votesCount: number;
  voted: boolean;
}

export interface ThreadComment {
  id: string;
  threadId: string;
  authorId: string;
  authorUsername: string;
  authorDisplayName: string;
  authorProfilePictureUrl: string | null;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateThreadRequest {
  content: string;
  mediaUrls?: string[];
  poll?: CreatePollRequest;
}

export interface CreatePollRequest {
  question: string;
  options: string[];
  expiresInHours: number;
}

export interface EditThreadRequest {
  content: string;
}

export interface QuoteThreadRequest {
  content: string;
}

export interface CreateCommentRequest {
  content: string;
}

export interface EditCommentRequest {
  content: string;
}

export interface ReactRequest {
  reactionType: ReactionType;
}

export interface ReportRequest {
  reason: string;
  details?: string;
}
