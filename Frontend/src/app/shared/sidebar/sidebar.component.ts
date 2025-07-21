import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';

interface MenuItem {
  title: string;
  icon: string;
  route: string;
  badge?: string | null;
  children?: MenuItem[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    MatExpansionModule
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  @Input() opened: boolean = true;

  menuItems: MenuItem[] = [
    {
      title: 'Dashboard',
      icon: 'dashboard',
      route: '/dashboard',
      badge: null
    },
    {
      title: 'Tareas',
      icon: 'assignment',
      route: '/tasks',
      children: [
        { title: 'Todas las Tareas', route: '/tasks', icon: 'list' },
        { title: 'Crear Tarea', route: '/tasks/create', icon: 'add' }
      ]
    },
    {
      title: 'Reportes',
      icon: 'analytics',
      route: '/reports',
      badge: null,
      children: [
        { title: 'Productividad', route: '/reports/productivity', icon: 'trending_up' },
        { title: 'Tareas por Estado', route: '/reports/status', icon: 'pie_chart' },
        { title: 'Tareas por Usuario', route: '/reports/users', icon: 'person' }
      ]
    }
  ];

  constructor(private router: Router) {}

  isActive(route: string): boolean {
    return this.router.url.startsWith(route);
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}
