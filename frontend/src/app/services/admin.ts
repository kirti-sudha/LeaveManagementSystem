import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

// Define interfaces for clarity
export interface AdminUser {
  id: number;
  name: string;
  employeeId: string;
  officialEmail: string;
  role: string;
  department: string;
  designation: string;
  reportingManagerName?: string;
}

export interface Holiday {
  id: number;
  name: string;
  date: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = '/api/admin';

  constructor(private http: HttpClient) { }

  // User Management
  getUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${this.apiUrl}/users`);
  }

  

  createUser(userData: any): Observable<AdminUser> {
    return this.http.post<AdminUser>(`${this.apiUrl}/users`, userData);
  }


  updateUser(userId: number, userData: any): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.apiUrl}/users/${userId}`, userData);
  }

   deleteUser(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${userId}`);
  }

  // Holiday Management
  getHolidays(): Observable<Holiday[]> {
    return this.http.get<Holiday[]>(`${this.apiUrl}/holidays`);
  }

   deleteHoliday(holidayId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/holidays/${holidayId}`);
  }

  createHoliday(holidayData: { name: string, date: string }): Observable<Holiday> {
    return this.http.post<Holiday>(`${this.apiUrl}/holidays`, holidayData);
  }
}