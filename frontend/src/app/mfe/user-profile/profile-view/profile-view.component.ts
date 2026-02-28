import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../shared/services/auth.service';
import { UserService } from '../../../shared/services/user.service';
import { UserProfile } from '../../../shared/models/api.models';

@Component({
  selector: 'app-profile-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile-view.component.html',
  styleUrl: './profile-view.component.scss',
})
export class ProfileViewComponent implements OnInit {
  profile: UserProfile | null = null;
  loading = true;
  error = '';
  isOwnProfile = false;
  isFollowing = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    public authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.loadProfile(params['username']);
    });
  }

  loadProfile(username: string): void {
    this.loading = true;
    this.error = '';

    this.userService.getUserProfile(username).subscribe({
      next: (res) => {
        this.profile = res.data;
        this.isOwnProfile = this.authService.currentUser?.username === username;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.error = err.status === 404 ? 'User not found' : 'Failed to load profile';
      },
    });
  }

  onFollow(): void {
    if (!this.profile || !this.authService.isLoggedIn) return;
    this.userService.followUser(this.profile.id).subscribe({
      next: () => (this.isFollowing = true),
    });
  }

  onUnfollow(): void {
    if (!this.profile) return;
    this.userService.unfollowUser(this.profile.id).subscribe({
      next: () => (this.isFollowing = false),
    });
  }

  onEditProfile(): void {
    this.router.navigate(['/settings']);
  }
}
