import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';
import { EventEmitter } from '@angular/core';


@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class SidebarComponent implements OnInit {
  userRole: string | null = null;
  userName: string | null = null;
  employeeId: string | null = null;
  
  // We pass this event up to the main layout to open the profile modal
  // This is a simple alternative to a complex state management solution
  public static onOpenProfile = new EventEmitter<void>();

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.userRole = currentUser.role;
      this.userName = currentUser.name;
      this.employeeId = currentUser.employeeId;
    }
  }

  openProfileModal(): void {
    SidebarComponent.onOpenProfile.emit();
  }

  logout(): void {
    this.authService.logout();
  }

  // Helper functions for conditional links
  isManager(): boolean { return this.userRole === 'MANAGER' || this.userRole === 'ADMIN'; }
  isAdmin(): boolean { return this.userRole === 'ADMIN'; }
  isHr(): boolean { return this.userRole === 'HR' || this.userRole === 'ADMIN'; }
}