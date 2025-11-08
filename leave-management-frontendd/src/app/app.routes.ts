import { Routes } from '@angular/router';
import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';

export const routes: Routes = [
  {
    path: 'login',
    // Lazy load the login component
    loadComponent: () => import('./auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: '',
    // Lazy load the main layout
    loadComponent: () => import('./layout/main-layout/main-layout').then(m => m.MainLayoutComponent),
    canActivate: [authGuard], // Protect this route and all its children
    children: [
        { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
        {
            path: 'dashboard',
            // Lazy load the dashboard component
            loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.DashboardComponent)
        },
        
        {
            path: 'apply-leave',
            loadComponent: () => import('./pages/apply-leave/apply-leave').then(m => m.ApplyLeaveComponent)
        },
        {
            path: 'my-leaves',
            loadComponent: () => import('./pages/my-leaves/my-leaves').then(m => m.MyLeavesComponent)
        },
        {
    path: 'team-leaves',
    loadComponent: () => import('./pages/team-leaves/team-leaves').then(m => m.TeamLeavesComponent)
},
{
    path: 'admin',
    loadComponent: () => import('./pages/admin-panel/admin-panel').then(m => m.AdminPanelComponent),
  canActivate: [adminGuard] // Protect this route
},
{
  path: 'hr-dashboard',
  loadComponent: () => import('./pages/hr-dashboard/hr-dashboard').then(m => m.HrDashboardComponent),
  
},

 {
    path: 'reports',
    loadComponent: () => import('./pages/reports/reports').then(m => m.ReportsComponent)
        },
{
    path: 'apply-leave/edit/:id', // <-- NEW ROUTE for editing
    loadComponent: () => import('./pages/apply-leave/apply-leave').then(m => m.ApplyLeaveComponent)
}

    ]
  },
  // Redirect any unknown paths to the login page by default
  { path: '**', redirectTo: 'login' }
];