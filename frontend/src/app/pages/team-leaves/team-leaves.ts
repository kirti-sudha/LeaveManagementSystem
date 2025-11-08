import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { ManagerService } from '../../services/manager';
import { LeaveRequest } from '../../services/leave';
import { PdfExportService } from '../../services/pdf-export';

@Component({
  selector: 'app-team-leaves',
  standalone: true,
  imports: [CommonModule, DatePipe, TitleCasePipe],
  templateUrl: './team-leaves.html',
  styleUrl: './team-leaves.scss'
})
export class TeamLeavesComponent implements OnInit {
  teamLeaves: LeaveRequest[] = [];
  isLoading = true;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private managerService: ManagerService,
    private pdfExportService: PdfExportService 
  ) {}

  ngOnInit(): void {
    this.loadTeamLeaves();
  }

  loadTeamLeaves(): void {
    this.isLoading = true;
    this.managerService.getTeamLeaveRequests().subscribe({
      next: (data) => {
        this.teamLeaves = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load team leave requests.';
        this.isLoading = false;
      }
    });
  }

  handleAction(action: 'approve' | 'reject', leaveId: number): void {
    const operation = action === 'approve'
      ? this.managerService.approveLeave(leaveId)
      : this.managerService.rejectLeave(leaveId);

    operation.subscribe({
      next: () => {
        // Remove the actioned leave from the list for immediate UI feedback
        this.teamLeaves = this.teamLeaves.filter(l => l.id !== leaveId);
        this.successMessage = `Leave request has been successfully ${action}d.`;
      },
      error: (err) => {
        this.errorMessage = err.error.message || `Failed to ${action} leave request.`;
      }
    });
  }
  exportAsPdf(): void {
    const headers = ['Employee Name', 'Leave Type', 'Start Date', 'End Date', 'Reason'];
    const data = this.teamLeaves.map(leave => [
      leave.employeeName,
      leave.leaveType,
      leave.startDate,
      leave.endDate,
      leave.reason
    ]);

    this.pdfExportService.exportToPdf(headers, data, 'team-leave-report.pdf', 'Team Leave Report');
  }
}