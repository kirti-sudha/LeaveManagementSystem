import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AdminService,AdminUser,Holiday } from '../../services/admin';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.scss'
})
export class AdminPanelComponent implements OnInit {
  activeTab: 'users' | 'holidays' = 'users';
  
  // User Management
  users: AdminUser[] = [];
  userForm: FormGroup;
  showUserForm = false;
  isEditMode = false;
  userToEdit: AdminUser | null = null;

  // Holiday Management
  holidays: Holiday[] = [];
  holidayForm: FormGroup;

  // Shared properties
  successMessage = '';
  errorMessage = '';
  currentUserId: number | null = null;

  constructor(
    private adminService: AdminService, 
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    // Initialize User Form
    this.userForm = this.fb.group({
      name: ['', Validators.required],
      employeeId: ['', Validators.required],
      officialEmail: ['', [Validators.required, Validators.email]],
      password: [''], 
      role: ['EMPLOYEE', Validators.required],
      department: ['', Validators.required],
      designation: ['', Validators.required],
      dateOfJoining: ['', Validators.required],
      reportingManagerId: [null]
    });

    // Initialize Holiday Form
    this.holidayForm = this.fb.group({
      name: ['', Validators.required],
      date: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUsers();
    this.loadHolidays();
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.currentUserId = currentUser.userId;
    }
  }

  selectTab(tab: 'users' | 'holidays'): void {
    this.activeTab = tab;
  }

  // --- USER MANAGEMENT METHODS ---
  loadUsers(): void { this.adminService.getUsers().subscribe(data => this.users = data); }
  openForm(userToEdit: AdminUser | null): void {
    this.showUserForm = true;
    if (userToEdit) {
      this.isEditMode = true;
      this.userToEdit = userToEdit;
      this.userForm.get('password')?.clearValidators();
      this.userForm.patchValue({
        ...userToEdit,
        password: '',
        reportingManagerId: this.users.find(m => m.name === userToEdit.reportingManagerName)?.id || null
      });
    } else {
      this.isEditMode = false;
      this.userToEdit = null;
      this.userForm.get('password')?.setValidators(Validators.required);
      this.userForm.reset({ role: 'EMPLOYEE', reportingManagerId: null });
    }
    this.userForm.updateValueAndValidity();
  }
  closeForm(): void { this.showUserForm = false; this.isEditMode = false; this.userToEdit = null; }
  onUserSubmit(): void {
    if (!this.userForm.valid) { this.setError("Please fill all required fields correctly."); return; }
    if (this.isEditMode && this.userToEdit) {
      this.adminService.updateUser(this.userToEdit.id, this.userForm.value).subscribe({
        next: () => { this.loadUsers(); this.closeForm(); this.setSuccess('User updated successfully!'); },
        error: err => this.setError(err.error.message || 'Failed to update user.')
      });
    } else {
      this.adminService.createUser(this.userForm.value).subscribe({
        next: () => { this.loadUsers(); this.closeForm(); this.setSuccess('User created successfully!'); },
        error: err => this.setError(err.error.message || 'Failed to create user.')
      });
    }
  }
  onDeleteUser(userId: number, userName: string): void {
    if (!confirm(`Are you sure you want to delete the user "${userName}"?`)) { return; }
    this.adminService.deleteUser(userId).subscribe({
      next: () => { this.loadUsers(); this.setSuccess('User deleted successfully!'); },
      error: err => this.setError(err.error?.message || 'Failed to delete user.')
    });
  }

  // --- HOLIDAY MANAGEMENT METHODS ---
  loadHolidays(): void {
    this.adminService.getHolidays().subscribe(data => {
      this.holidays = data.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
    });
  }

  onHolidaySubmit(): void {
    // 1. Log that the function was called.
    console.log('onHolidaySubmit function triggered.');

    // 2. Log the form's status and value. This is the most important part.
    console.log('Is the holiday form valid?', this.holidayForm.valid);
    console.log('Holiday Form Value:', this.holidayForm.value);
    console.log('Holiday Form Errors:', this.holidayForm.errors);

    if (this.holidayForm.valid) {
      // 3. Log that we are about to call the service.
      console.log('Form is valid. Calling createHoliday service...');

      this.adminService.createHoliday(this.holidayForm.value).subscribe({
        next: () => {
          console.log('SUCCESS: createHoliday API call successful.');
          this.loadHolidays();
          this.holidayForm.reset();
          this.setSuccess('Holiday added successfully!');
        },
        error: err => {
          console.error('ERROR: createHoliday API call failed.', err);
          this.setError(err.error.message || 'Failed to add holiday.');
        }
      });
    } else {
      console.warn('Form is NOT valid. Submission blocked.');
    }
  }

  onDeleteHoliday(holidayId: number, holidayName: string): void {
    if (!confirm(`Are you sure you want to delete the holiday "${holidayName}"?`)) {
      return;
    }
    this.adminService.deleteHoliday(holidayId).subscribe({
      next: () => {
        this.loadHolidays();
        this.setSuccess('Holiday deleted successfully!');
      },
      error: err => this.setError(err.error.message || 'Failed to delete holiday.')
    });
  }
  
  // --- ALERT HELPER METHODS ---
  setSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = '';
    setTimeout(() => this.successMessage = '', 5000);
  }

  setError(message: string): void {
    this.errorMessage = message;
    this.successMessage = '';
    setTimeout(() => this.errorMessage = '', 5000);
  }
}