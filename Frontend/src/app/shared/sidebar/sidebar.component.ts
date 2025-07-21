import { Component, Input, Output, EventEmitter, HostListener, ViewChild, AfterViewInit, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { MatSidenav } from '@angular/material/sidenav';
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
export class SidebarComponent implements OnInit {
  @Input() opened: boolean = true;
  @Output() closeSidebar = new EventEmitter<void>();

  isMobile: boolean = false;

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
        { title: 'Informe', route: '/reports/productivity', icon: 'picture_as_pdf' },
      ]
    }
  ];

  constructor(private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.checkScreenSize();
    // Cerrar automáticamente en móvil al navegar
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd && this.isMobile && this.opened) {
        this.closeSidebar.emit();
      }
    });
  }

  @HostListener('window:resize')
  onResize() {
    this.checkScreenSize();
  }

  checkScreenSize() {
    this.isMobile = window.innerWidth <= 768;
    this.cdr.detectChanges();
  }

  isActive(route: string): boolean {
    return this.router.url.startsWith(route);
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    if (this.isMobile) {
      this.closeSidebar.emit();
    }
  }
}
