import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { HrService } from '../../services/hr';
import { LeaveRequest } from '../../services/leave';
import { PdfExportService } from '../../services/pdf-export';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-hr-dashboard',
  standalone: true,
  imports: [CommonModule, DatePipe, TitleCasePipe, ReactiveFormsModule],
  templateUrl: './hr-dashboard.html',
  styleUrl: './hr-dashboard.scss'
})
export class HrDashboardComponent implements OnInit {
  allLeaves: LeaveRequest[] = [];
  filteredLeaves: LeaveRequest[] = [];
  isLoading = true;
  statusFilter = new FormControl('ALL');
  searchControl = new FormControl('');
   successMessage: string = '';
  errorMessage: string = '';
  

  constructor(
    private hrService: HrService,
    private pdfExportService: PdfExportService
  ) {}

  ngOnInit(): void {
    this.loadAllLeaves();
    this.setupFilters();
  }

  loadAllLeaves(): void {
    this.isLoading = true;
    this.hrService.getAllLeaveRequests().subscribe(data => {
      this.allLeaves = data;
      this.applyFilters(); // Apply initial filters
      this.isLoading = false;
    });
  }
  
  setupFilters(): void {
    this.statusFilter.valueChanges.subscribe(() => this.applyFilters());
    this.searchControl.valueChanges.pipe(
      debounceTime(300), // Wait for user to stop typing
      distinctUntilChanged()
    ).subscribe(() => this.applyFilters());
  }
  
  applyFilters(): void {
    const status = this.statusFilter.value;
    const searchTerm = this.searchControl.value?.toLowerCase() || '';

    this.filteredLeaves = this.allLeaves.filter(leave => {
      const statusMatch = status === 'ALL' || leave.status === status;
      const searchMatch = leave.employeeName.toLowerCase().includes(searchTerm);
      return statusMatch && searchMatch;
    });
  }

 handleAction(action: 'approve' | 'reject', leaveId: number): void {
    const operation = action === 'approve'
      ? this.hrService.approveLeave(leaveId)
      : this.hrService.rejectLeave(leaveId);

    operation.subscribe({
      next: updatedLeave => {
        const index = this.allLeaves.findIndex(l => l.id === leaveId);
        if (index !== -1) {
          this.allLeaves[index] = updatedLeave;
        }
        this.applyFilters();
        this.setSuccess(`Leave request has been successfully ${action}d.`); // Add success message
      },
      error: err => this.setError(err.error.message || `Failed to ${action} leave request.`) // Add error message
    });
  }
  
  // Add helper methods for alerts
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

  exportPdf(): void {
    const headers = ['Employee Name', 'Leave Type', 'Start Date', 'End Date', 'Status', 'Reason'];
    const data = this.filteredLeaves.map(leave => [
      leave.employeeName,
      leave.leaveType,
      leave.startDate,
      leave.endDate,
      leave.status,
      leave.reason
    ]);

    this.pdfExportService.exportToPdf(headers, data, 'hr-leave-report.pdf', 'Comprehensive Leave Report');
  }
}