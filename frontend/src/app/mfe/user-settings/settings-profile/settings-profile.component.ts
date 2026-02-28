import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../shared/services/auth.service';
import { UserService } from '../../../shared/services/user.service';

@Component({
  selector: 'app-settings-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './settings-profile.component.html',
  styleUrl: './settings-profile.component.scss',
})
export class SettingsProfileComponent implements OnInit {
  profileForm!: FormGroup;
  loading = true;
  saving = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.profileForm = this.fb.group({
      displayName: [''],
      username: [''],
      bio: [''],
      location: [''],
    });

    const username = this.authService.currentUser?.username;
    if (username) {
      this.userService.getUserProfile(username).subscribe({
        next: (res) => {
          this.profileForm.patchValue({
            displayName: res.data.displayName || '',
            username: res.data.username || '',
            bio: res.data.bio || '',
            location: res.data.location || '',
          });
          this.loading = false;
        },
        error: () => {
          this.loading = false;
          this.errorMessage = 'Failed to load profile';
        },
      });
    }
  }

  onSave(): void {
    this.saving = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.userService.updateProfile(this.profileForm.value).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Profile updated successfully';
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to update profile';
      },
    });
  }
}
