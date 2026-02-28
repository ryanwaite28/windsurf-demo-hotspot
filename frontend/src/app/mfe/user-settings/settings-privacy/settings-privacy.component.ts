import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../../shared/services/user.service';
import { VisibilitySetting } from '../../../shared/models/api.models';

@Component({
  selector: 'app-settings-privacy',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './settings-privacy.component.html',
  styleUrl: './settings-privacy.component.scss',
})
export class SettingsPrivacyComponent implements OnInit {
  privacyForm!: FormGroup;
  loading = true;
  saving = false;
  successMessage = '';
  errorMessage = '';

  visibilityOptions: { value: VisibilitySetting; label: string }[] = [
    { value: 'PUBLIC', label: 'Public' },
    { value: 'FOLLOWERS', label: 'Followers Only' },
    { value: 'NOBODY', label: 'Nobody' },
  ];

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
  ) {}

  ngOnInit(): void {
    this.privacyForm = this.fb.group({
      profileVisibility: ['PUBLIC'],
      postsVisibility: ['PUBLIC'],
      locationVisibility: ['PUBLIC'],
      phoneVisibility: ['NOBODY'],
    });

    this.userService.getPrivacySettings().subscribe({
      next: (res) => {
        this.privacyForm.patchValue(res.data);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to load privacy settings';
      },
    });
  }

  onSave(): void {
    this.saving = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.userService.updatePrivacySettings(this.privacyForm.value).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Privacy settings updated';
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to update privacy settings';
      },
    });
  }
}
