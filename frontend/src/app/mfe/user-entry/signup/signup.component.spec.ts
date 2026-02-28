import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SignupComponent } from './signup.component';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignupComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have an invalid form when empty', () => {
    expect(component.signupForm.valid).toBeFalse();
  });

  it('should require all fields', () => {
    expect(component.signupForm.controls['email'].errors?.['required']).toBeTruthy();
    expect(component.signupForm.controls['displayName'].errors?.['required']).toBeTruthy();
    expect(component.signupForm.controls['username'].errors?.['required']).toBeTruthy();
    expect(component.signupForm.controls['password'].errors?.['required']).toBeTruthy();
    expect(component.signupForm.controls['confirmPassword'].errors?.['required']).toBeTruthy();
  });

  it('should validate password mismatch', () => {
    component.signupForm.controls['email'].setValue('test@test.com');
    component.signupForm.controls['displayName'].setValue('Test');
    component.signupForm.controls['username'].setValue('testuser');
    component.signupForm.controls['password'].setValue('Password123!');
    component.signupForm.controls['confirmPassword'].setValue('Different456!');
    expect(component.signupForm.errors?.['passwordMismatch']).toBeTruthy();
  });

  it('should validate matching passwords', () => {
    component.signupForm.controls['email'].setValue('test@test.com');
    component.signupForm.controls['displayName'].setValue('Test');
    component.signupForm.controls['username'].setValue('testuser');
    component.signupForm.controls['password'].setValue('Password123!');
    component.signupForm.controls['confirmPassword'].setValue('Password123!');
    expect(component.signupForm.valid).toBeTrue();
  });

  it('should validate username pattern', () => {
    component.signupForm.controls['username'].setValue('invalid user!');
    expect(component.signupForm.controls['username'].errors?.['pattern']).toBeTruthy();
  });

  it('should not submit when form is invalid', () => {
    component.onSubmit();
    expect(component.loading).toBeFalse();
  });
});
