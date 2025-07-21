import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface DashboardStats {
  totalTasks: number;
  pendingTasks: number;
  completedTasks: number;
  overdueTasks: number;
}

export interface TaskSummary {
  taskId: string;
  title: string;
  taskStatus: {
    statusName: string;
    colorHex: string;
  };
  taskPriority: {
    priorityName: string;
    colorHex: string;
  };
  dueDate?: string;
  assignedTo?: {
    firstName: string;
    lastName: string;
  };
}

export interface DashboardData {
  taskStats: DashboardStats;
  recentTasks: TaskSummary[];
  upcomingTasks: TaskSummary[];
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard';
  
  private dashboardDataSignal = signal<DashboardData | null>(null);
  private loadingSignal = signal<boolean>(false);
  private errorSignal = signal<string | null>(null);

  public dashboardData = this.dashboardDataSignal.asReadonly();
  public loading = this.loadingSignal.asReadonly();
  public error = this.errorSignal.asReadonly();

  public stats = computed(() => this.dashboardDataSignal()?.taskStats);
  public recentTasks = computed(() => this.dashboardDataSignal()?.recentTasks || []);
  public upcomingTasks = computed(() => this.dashboardDataSignal()?.upcomingTasks || []);

  constructor(private http: HttpClient) {}

  loadDashboardData(): Observable<DashboardData> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.get<DashboardData>(this.apiUrl).pipe(
      tap({
        next: (data) => {
          this.dashboardDataSignal.set(data);
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al cargar datos del dashboard');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  refreshDashboard(): void {
    this.loadDashboardData().subscribe();
  }

  clearError(): void {
    this.errorSignal.set(null);
  }

  getStatusDistribution(): Observable<{ [status: string]: number }> {
    return this.http.get<{ [status: string]: number }>(`${this.apiUrl}/stats/status-distribution`);
  }

  getPriorityDistribution(): Observable<{ [priority: string]: number }> {
    return this.http.get<{ [priority: string]: number }>(`${this.apiUrl}/stats/priority-distribution`);
  }
}
