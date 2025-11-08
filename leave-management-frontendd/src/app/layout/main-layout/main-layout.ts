import { Component, OnInit, OnDestroy } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from '../../shared/sidebar/sidebar';
import { ModalComponent } from '../../shared/modal/modal';
import { ProfileComponent } from '../../shared/profile/profile';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent, ModalComponent, ProfileComponent],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.scss'
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  isProfileModalVisible = false;
  private profileSubscription!: Subscription;

  ngOnInit(): void {
    // Subscribe to the static event emitter
    this.profileSubscription = SidebarComponent.onOpenProfile.subscribe(() => {
      this.isProfileModalVisible = true;
    });
  }

  ngOnDestroy(): void {
    // Clean up the subscription to prevent memory leaks
    if (this.profileSubscription) {
      this.profileSubscription.unsubscribe();
    }
  }

  closeProfileModal(): void {
    this.isProfileModalVisible = false;
  }
}