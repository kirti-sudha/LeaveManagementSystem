import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { UserService,UserProfile } from '../../services/user';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.scss'
})
export class ProfileComponent implements OnInit {
  profile: UserProfile | null = null;
  editableProfile: UserProfile | null = null;
  isEditing = false;
  successMessage = '';
  errorMessage = '';
  isAdmin = false;

  constructor(private userService: UserService, private authService: AuthService) {}

  ngOnInit(): void {
    this.loadProfile();
    this.isAdmin = this.authService.getRole() === 'ADMIN';
  }

  loadProfile(): void {
    this.userService.getProfile().subscribe({
      next: (data) => {
        this.profile = data;
        // Create a copy for editing to allow cancellation
        this.editableProfile = { ...data };
      },
      error: (err) => {
        this.errorMessage = 'Failed to load profile.';
        console.error(err);
      }
    });
  }

  toggleEdit(): void {
    this.isEditing = !this.isEditing;
    if (!this.isEditing) {
      // Reset changes if user cancels
      this.editableProfile = this.profile ? { ...this.profile } : null;
    }
  }

  onSubmit(profileForm: NgForm): void {
    if (profileForm.invalid || !this.editableProfile) {
      this.errorMessage = 'Please fill all required fields.';
      return;
    }

    // Clear previous messages
    this.successMessage = '';
    this.errorMessage = '';

    // Clear password if it's empty to avoid sending an empty string
    if (this.editableProfile.password === '') {
      delete this.editableProfile.password;
    }

    this.userService.updateProfile(this.editableProfile).subscribe({
      next: (updatedProfile) => {
        this.profile = updatedProfile;
        this.editableProfile = { ...updatedProfile };
        this.isEditing = false;
        this.successMessage = 'Profile updated successfully!';
      },
      error: (err) => {
        this.errorMessage = 'Failed to update profile. Please try again.';
        console.error(err);
      }
    });
  }
}