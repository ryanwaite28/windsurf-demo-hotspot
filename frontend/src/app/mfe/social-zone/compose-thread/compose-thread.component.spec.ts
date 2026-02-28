import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComposeThreadComponent } from './compose-thread.component';

describe('ComposeThreadComponent', () => {
  let component: ComposeThreadComponent;
  let fixture: ComponentFixture<ComposeThreadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComposeThreadComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(ComposeThreadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have an invalid form when empty', () => {
    expect(component.composeForm.valid).toBeFalse();
  });

  it('should have a valid form with content', () => {
    component.composeForm.controls['content'].setValue('Hello world');
    expect(component.composeForm.valid).toBeTrue();
  });

  it('should not submit when form is invalid', () => {
    component.onPost();
    expect(component.posting).toBeFalse();
  });

  it('should validate max length', () => {
    component.composeForm.controls['content'].setValue('a'.repeat(501));
    expect(component.composeForm.controls['content'].errors?.['maxlength']).toBeTruthy();
  });
});
