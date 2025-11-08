import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, TitleCasePipe } from '@angular/common';
import { AuthService } from '../../services/auth';
import { LeaveService,LeaveRequest } from '../../services/leave';
import { ManagerService } from '../../services/manager';
import { HrService } from '../../services/hr';
import { PdfExportService } from '../../services/pdf-export';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, DatePipe, TitleCasePipe],
  templateUrl: './reports.html',
  styleUrl: './reports.scss'
})
export class ReportsComponent implements OnInit {
  userRole: string | null = null;
  reportData: LeaveRequest[] = [];
  isLoading = true;
  reportTitle = 'Leave Report';

  constructor(
    private authService: AuthService,
    private leaveService: LeaveService,
    private managerService: ManagerService,
    private hrService: HrService,
    private pdfExportService: PdfExportService
  ) {}

  ngOnInit(): void {
    this.userRole = this.authService.getRole();
    this.loadReportData();
  }

    loadReportData(): void {
    this.isLoading = true;
    switch (this.userRole) {
      case 'ADMIN':
      case 'HR':
        this.reportTitle = 'Comprehensive Leave Report';
        this.hrService.getAllLeaveRequests().subscribe(data => this.setData(data));
        break;
      case 'MANAGER':
        this.reportTitle = 'Team Leave Report';
        // --- THIS IS THE CRUCIAL CHANGE ---
        // Call the new history endpoint instead of the pending-only endpoint
        this.managerService.getTeamLeaveHistory().subscribe(data => this.setData(data));
        break;
      case 'EMPLOYEE':
      default:
        this.reportTitle = 'My Leave Report';
        this.leaveService.getMyLeaves().subscribe(data => this.setData(data));
        break;
    }
  }

  private setData(data: LeaveRequest[]): void {
    this.reportData = data;
    this.isLoading = false;
  }

  exportAsPdf(): void {
    // Dynamically set headers based on the role
    let headers: string[];
    let data: any[][];

    if (this.userRole === 'EMPLOYEE') {
      headers = ['Leave Type', 'Start Date', 'End Date', 'Status', 'Reason'];
      data = this.reportData.map(leave => [
        leave.leaveType, leave.startDate, leave.endDate, leave.status, leave.reason
      ]);
    } else {
      // For Manager, HR, Admin, we want to include the employee name
      headers = ['Employee Name', 'Leave Type', 'Start Date', 'End Date', 'Status', 'Reason'];
      data = this.reportData.map(leave => [
        leave.employeeName, leave.leaveType, leave.startDate, leave.endDate, leave.status, leave.reason
      ]);
    }

    this.pdfExportService.exportToPdf(headers, data, 'leave-report.pdf', this.reportTitle);
  }
}