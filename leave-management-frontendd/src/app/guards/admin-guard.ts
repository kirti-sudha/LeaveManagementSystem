import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  if (authService.isLoggedIn() && authService.getRole() === 'ADMIN') {
    return true; // User is an admin, allow access
  }
  
  // Not an admin, redirect to their default dashboard
  router.navigate(['/dashboard']); 
  return false;
};