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
import { ApexNonAxisChartSeries, ApexChart, ApexLegend, ApexTitleSubtitle } from 'ng-apexcharts';
import { Router } from '@angular/router';

export type ApexOptions = {
  series: ApexNonAxisChartSeries;
  chart: ApexChart;
  labels: string[];
  legend: ApexLegend;
  title?: ApexTitleSubtitle;
};

@Component({
  selector: 'app-dashboard-main',
  templateUrl: './dashboard-main.component.html',
  styleUrls: ['./dashboard-main.component.scss']
})
export class DashboardMainComponent implements OnInit {
  priorityLabels: string[] = [];
  priorityData: number[] = [];
  priorityChartOptions: Partial<ApexOptions> = {};
  statusLabels: string[] = [];
  statusData: number[] = [];
  statusChartOptions: Partial<ApexOptions> = {};
  constructor(
    private dashboardService: DashboardService,
    private taskService: TaskService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
    this.dashboardService.getPriorityDistribution().subscribe(data => {
      this.priorityChartOptions = {
        series: Object.values(data),
        chart: { type: 'donut', width: 350, height: 250 },
        labels: Object.keys(data),
        legend: { position: 'bottom' }
      };
    });
    this.dashboardService.getStatusDistribution().subscribe(data => {
      this.statusChartOptions = {
        series: Object.values(data),
        chart: { type: 'donut', width: 350, height: 250 },
        labels: Object.keys(data),
        legend: { position: 'bottom' }
      };
    });
  }

  loadDashboardData(): void {
    this.dashboardService.loadDashboardData().subscribe();
    this.taskService.loadTasks().subscribe();
  }

  get stats() {
    return this.dashboardService.stats();
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

  onViewTask(task: any) {
    this.router.navigate(['/tasks', task.taskId]);
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
