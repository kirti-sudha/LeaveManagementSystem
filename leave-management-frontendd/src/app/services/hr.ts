import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LeaveRequest } from './leave'; // Re-use interface
import { AdminUser } from './admin';   // Re-use interface

@Injectable({
  providedIn: 'root'
})
export class HrService {
  private apiUrl = '/api/hr';

  constructor(private http: HttpClient) { }

  getAllLeaveRequests(): Observable<LeaveRequest[]> {
    return this.http.get<LeaveRequest[]>(`${this.apiUrl}/leaves`);
  }

  approveLeave(leaveId: number): Observable<LeaveRequest> {
    return this.http.put<LeaveRequest>(`${this.apiUrl}/leaves/${leaveId}/approve`, {});
  }

  rejectLeave(leaveId: number): Observable<LeaveRequest> {
    return this.http.put<LeaveRequest>(`${this.apiUrl}/leaves/${leaveId}/reject`, {});
  }
  
  getEmployeesAndManagers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/employees`);
  }
}