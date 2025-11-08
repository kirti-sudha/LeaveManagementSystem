import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router'; // Import RouterOutlet

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet // Add it here
  ],
  template: '<router-outlet></router-outlet>', // Simplified template
})
export class AppComponent {
  title = 'leave-management-frontend';
}