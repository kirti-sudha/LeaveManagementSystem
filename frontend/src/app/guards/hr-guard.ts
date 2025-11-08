import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const hrGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const userRole = authService.getRole();
  
  if (authService.isLoggedIn() && (userRole === 'HR' || userRole === 'ADMIN')) {
    return true;
  }
  
  router.navigate(['/dashboard']);
  return false;
};