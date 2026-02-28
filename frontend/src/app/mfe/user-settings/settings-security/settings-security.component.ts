import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../../shared/services/user.service';

@Component({
  selector: 'app-settings-security',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './settings-security.component.html',
  styleUrl: './settings-security.component.scss',
})
export class SettingsSecurityComponent implements OnInit {
  securityForm!: FormGroup;
  loading = true;
  saving = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
  ) {}

  ngOnInit(): void {
    this.securityForm = this.fb.group({
      twoFactorEnabled: [false],
    });

    this.userService.getSecuritySettings().subscribe({
      next: (res) => {
        this.securityForm.patchValue(res.data);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Failed to load security settings';
      },
    });
  }

  onSave(): void {
    this.saving = true;
    this.successMessage = '';
    this.errorMessage = '';

    this.userService.updateSecuritySettings(this.securityForm.value).subscribe({
      next: () => {
        this.saving = false;
        this.successMessage = 'Security settings updated';
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err.error?.message || 'Failed to update security settings';
      },
    });
  }
}
