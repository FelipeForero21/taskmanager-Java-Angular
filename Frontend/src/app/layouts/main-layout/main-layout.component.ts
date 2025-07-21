import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { HeaderComponent } from '../../shared/header/header.component';
import { SidebarComponent } from '../../shared/sidebar/sidebar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    MatSidenavModule,
    HeaderComponent,
    SidebarComponent
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {
  sidebarOpened = signal(true);

  toggleSidebar(): void {
    console.log('toggleSidebar antes:', this.sidebarOpened());
    this.sidebarOpened.update(opened => !opened);
    console.log('toggleSidebar después:', this.sidebarOpened());
  }

  onSidebarToggle(): void {
    console.log('onSidebarToggle');
    this.toggleSidebar();
  }
}
