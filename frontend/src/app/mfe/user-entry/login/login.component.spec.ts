import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have an invalid form when empty', () => {
    expect(component.loginForm.valid).toBeFalse();
  });

  it('should require email', () => {
    const email = component.loginForm.controls['email'];
    expect(email.errors?.['required']).toBeTruthy();
  });

  it('should validate email format', () => {
    const email = component.loginForm.controls['email'];
    email.setValue('not-an-email');
    expect(email.errors?.['email']).toBeTruthy();
  });

  it('should require password', () => {
    const password = component.loginForm.controls['password'];
    expect(password.errors?.['required']).toBeTruthy();
  });

  it('should have a valid form with correct inputs', () => {
    component.loginForm.controls['email'].setValue('test@test.com');
    component.loginForm.controls['password'].setValue('Password123!');
    expect(component.loginForm.valid).toBeTrue();
  });

  it('should not submit when form is invalid', () => {
    component.onSubmit();
    expect(component.loading).toBeFalse();
  });
});
