import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LeaveRequest } from './leave'; // Re-use the same interface

@Injectable({
  providedIn: 'root'
})
export class ManagerService {
  private apiUrl = '/api/manager';

  constructor(private http: HttpClient) { }

  getTeamLeaveRequests(): Observable<LeaveRequest[]> {
    return this.http.get<LeaveRequest[]>(`${this.apiUrl}/leave-requests`);
  }

  getTeamLeaveHistory(): Observable<LeaveRequest[]> {
    return this.http.get<LeaveRequest[]>(`${this.apiUrl}/team-leave-history`);
  }

  approveLeave(leaveId: number): Observable<LeaveRequest> {
    return this.http.put<LeaveRequest>(`${this.apiUrl}/leave-requests/${leaveId}/approve`, {});
  }

  rejectLeave(leaveId: number): Observable<LeaveRequest> {
    return this.http.put<LeaveRequest>(`${this.apiUrl}/leave-requests/${leaveId}/reject`, {});
  }
}