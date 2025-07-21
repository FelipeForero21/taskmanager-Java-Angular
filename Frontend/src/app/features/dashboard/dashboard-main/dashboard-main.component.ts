import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { DashboardService } from '../../../core/dashboard.service';
import { TaskService } from '../../../core/task.service';

@Component({
  selector: 'app-dashboard-main',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatDividerModule,
    MatListModule
  ],
  templateUrl: './dashboard-main.component.html',
  styleUrl: './dashboard-main.component.scss'
})
export class DashboardMainComponent implements OnInit {
  constructor(
    private dashboardService: DashboardService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.dashboardService.loadDashboardData().subscribe();
    this.dashboardService.loadProductivityMetrics().subscribe();
    this.taskService.loadTasks().subscribe();
  }

  get stats() {
    return this.dashboardService.stats();
  }

  get productivityMetrics() {
    return this.dashboardService.productivityMetrics();
  }

  get recentTasks() {
    return this.dashboardService.recentTasks();
  }

  get upcomingTasks() {
    return this.dashboardService.upcomingTasks();
  }

  get loading() {
    return this.dashboardService.loading();
  }

  get error() {
    return this.dashboardService.error();
  }

  refreshDashboard(): void {
    this.dashboardService.refreshDashboard();
  }

  getCompletionRateColor(rate: number): string {
    if (rate >= 80) return 'primary';
    if (rate >= 60) return 'accent';
    return 'warn';
  }

  getPriorityColor(priority: string): string {
    switch (priority.toLowerCase()) {
      case 'alta': return 'warn';
      case 'media': return 'accent';
      case 'baja': return 'primary';
      default: return 'primary';
    }
  }

  getStatusColor(status: string): string {
    switch (status.toLowerCase()) {
      case 'completada': return 'primary';
      case 'en progreso': return 'accent';
      case 'pendiente': return 'warn';
      default: return 'primary';
    }
  }
}
