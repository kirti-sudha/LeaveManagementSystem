import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { FormsModule } from '@angular/forms'; // <-- Import for ngModel
import { CommonModule } from '@angular/common'; // <-- Import for *ngIf

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,  // <-- Add to imports array
    CommonModule  // <-- Add to imports array
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {
  credentials = {
    email: '',
    password: ''
  };
  errorMessage: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

// in login.component.ts

onSubmit(): void {
  this.errorMessage = null;
  this.authService.login(this.credentials).subscribe({
    next: (response) => {
      // --- SMART REDIRECT LOGIC ---
      const userRole = response.role;
      switch (userRole) {
        case 'ADMIN':
          this.router.navigate(['/admin']);
          break;
        case 'HR':
          this.router.navigate(['/hr-dashboard']);
          break;
        case 'MANAGER':
          this.router.navigate(['/team-leaves']);
          break;
        case 'EMPLOYEE':
        default:
          this.router.navigate(['/dashboard']);
          break;
      }
    },
    error: (err) => {
      this.errorMessage = 'Invalid email or password. Please try again.';
    }
  });
}
}