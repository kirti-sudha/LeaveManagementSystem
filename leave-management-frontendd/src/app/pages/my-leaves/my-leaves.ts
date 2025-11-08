import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs'; // Import forkJoin for parallel API calls
import { LeaveService,LeaveRequest } from '../../services/leave';
import { UserService,LeaveBalance } from '../../services/user';

@Component({
  selector: 'app-my-leaves',
  standalone: true,
  imports: [CommonModule, TitleCasePipe, DatePipe, RouterModule],
  templateUrl: './my-leaves.html',
  styleUrl: './my-leaves.scss'
})
export class MyLeavesComponent implements OnInit {
  myLeaves: LeaveRequest[] = [];
  leaveBalances: LeaveBalance[] = [];
  isLoading = true;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private leaveService: LeaveService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadPageData();
  }

  loadPageData(): void {
    this.isLoading = true;
    this.errorMessage = null;

    // Use forkJoin to make both API calls in parallel
    forkJoin({
      balances: this.userService.getLeaveBalances(),
      leaves: this.leaveService.getMyLeaves()
    }).subscribe({
      next: (results) => {
        this.leaveBalances = results.balances;
        this.myLeaves = results.leaves;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load page data. Please try again later.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  onCancelLeave(leaveId: number): void {
    if (!confirm('Are you sure you want to cancel this leave request?')) {
      return;
    }

    this.successMessage = null;
    this.errorMessage = null;

    this.leaveService.cancelLeave(leaveId).subscribe({
      next: (updatedLeave) => {
        const index = this.myLeaves.findIndex(l => l.id === leaveId);
        if (index !== -1) {
          this.myLeaves[index] = updatedLeave;
        }
        this.successMessage = 'Leave request cancelled successfully.';
        // Optionally, reload everything to ensure balance consistency if cancellations should return days
        // this.loadPageData(); 
      },
      error: (err) => {
        this.errorMessage = err.error.message || 'Failed to cancel leave request.';
      }
    });
  }
}