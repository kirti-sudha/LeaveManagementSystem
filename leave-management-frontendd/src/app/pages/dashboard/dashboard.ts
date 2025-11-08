import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService,LeaveBalance } from '../../services/user'; // Assuming you added it here

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {
  leaveBalances: LeaveBalance[] = [];
  isLoading = true;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getLeaveBalances().subscribe(data => {
      this.leaveBalances = data;
      this.isLoading = false;
    });
  }
}