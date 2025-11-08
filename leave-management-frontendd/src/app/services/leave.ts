import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// Interfaces matching our backend DTOs
export interface LeaveRequest {
  id: number;
  employeeName: string;
  leaveType: string;
  startDate: string;
  endDate: string;
  isHalfDay: boolean;
  reason: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
  documentPath?: string;
}

// A type for the leave application form data
export interface LeaveApplication {
    leaveType: string;
    startDate: string;
    endDate: string;
    isHalfDay: boolean;
    reason: string;
    document?: File;
}

@Injectable({
  providedIn: 'root'
})
export class LeaveService {
  private apiUrl = '/api/leaves';

  constructor(private http: HttpClient) { }

  /**
   * Applies for a new leave. Uses FormData to support file uploads.
   * @param application The leave application data.
   */
  applyForLeave(application: LeaveApplication): Observable<LeaveRequest> {
    const formData = new FormData();

    formData.append('leaveType', application.leaveType);
    formData.append('startDate', application.startDate);
    formData.append('endDate', application.endDate);
    
    // --- THIS IS THE MODIFIED LINE ---
    // Use a ternary operator to guarantee a 'true' or 'false' string.
    const isHalfDayValue = application.isHalfDay ? 'true' : 'false';
    formData.append('isHalfDay', isHalfDayValue);
    
    formData.append('reason', application.reason);

    if (application.document) {
      formData.append('document', application.document, application.document.name);
    }

    return this.http.post<LeaveRequest>(`${this.apiUrl}/apply`, formData);
  }

  /**
   * Gets all leave requests for the currently logged-in user.
   */
  getMyLeaves(): Observable<LeaveRequest[]> {
    return this.http.get<LeaveRequest[]>(`${this.apiUrl}/my-leaves`);
  }

  /**
   * Cancels a pending leave request.
   * @param leaveId The ID of the leave to cancel.
   */
  cancelLeave(leaveId: number): Observable<LeaveRequest> {
    return this.http.put<LeaveRequest>(`${this.apiUrl}/${leaveId}/cancel`, {});
  }

  getLeaveById(id: number): Observable<LeaveRequest> {
    return this.http.get<LeaveRequest>(`${this.apiUrl}/${id}`);
  }

  editLeave(id: number, application: LeaveApplication): Observable<LeaveRequest> {
    const formData = new FormData();

    formData.append('leaveType', application.leaveType);
    formData.append('startDate', application.startDate);
    formData.append('endDate', application.endDate);
    
    // --- THIS IS THE SECOND MODIFIED LINE ---
    // Apply the same fix here for the edit method.
    const isHalfDayValue = application.isHalfDay ? 'true' : 'false';
    formData.append('isHalfDay', isHalfDayValue);
    
    formData.append('reason', application.reason);

    if (application.document) {
      formData.append('document', application.document, application.document.name);
    }
    
    return this.http.put<LeaveRequest>(`${this.apiUrl}/${id}`, formData);
  }
}