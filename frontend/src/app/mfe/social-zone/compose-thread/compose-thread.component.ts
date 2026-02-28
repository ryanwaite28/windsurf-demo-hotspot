import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ThreadService } from '../../../shared/services/thread.service';
import { Thread } from '../../../shared/models/social.models';

@Component({
  selector: 'app-compose-thread',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './compose-thread.component.html',
  styleUrl: './compose-thread.component.scss',
})
export class ComposeThreadComponent {
  @Output() threadCreated = new EventEmitter<Thread>();

  composeForm: FormGroup;
  posting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private threadService: ThreadService,
  ) {
    this.composeForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(500)]],
    });
  }

  onPost(): void {
    if (this.composeForm.invalid) return;

    this.posting = true;
    this.errorMessage = '';

    this.threadService.createThread(this.composeForm.value).subscribe({
      next: (res) => {
        this.posting = false;
        this.composeForm.reset();
        this.threadCreated.emit(res.data);
      },
      error: (err) => {
        this.posting = false;
        this.errorMessage = err.error?.message || 'Failed to create thread';
      },
    });
  }
}
