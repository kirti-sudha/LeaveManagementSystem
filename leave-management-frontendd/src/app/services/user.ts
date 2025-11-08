import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// It's good practice to define an interface for our DTO
export interface UserProfile {
  // Non-editable
  employeeId: string;
  department: string;
  designation: string;
  reportingManagerName: string;
  role: string;
  officialEmail: string;
  dateOfJoining: string; // Comes as string from JSON

  // Editable
  name: string;
  phoneNumber: string;
  address: string;
  emergencyContact: string;
  password?: string; // Optional for updates
}
export interface LeaveBalance {
  leaveType: string;
  totalDays: number;
  remainingDays: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient) { }

  

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/profile`);
  }

  updateProfile(profileData: UserProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.apiUrl}/profile`, profileData);
  }

  getLeaveBalances(): Observable<LeaveBalance[]> {
    // Assuming a new backend endpoint /api/users/leave-balances
    // We need to create this endpoint in Spring Boot's UserController
    return this.http.get<LeaveBalance[]>(`/api/users/leave-balances`);
  }

}