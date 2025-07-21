import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface DashboardStats {
  totalTasks: number;
  pendingTasks: number;
  completedTasks: number;
  overdueTasks: number;
  highPriorityTasks: number;
  completionRate: number;
  averageCompletionTime: number;
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
  stats: DashboardStats;
  recentTasks: TaskSummary[];
  upcomingTasks: TaskSummary[];
  weeklyProgress: {
    date: string;
    completed: number;
    total: number;
  }[];
}

export interface ProductivityMetrics {
  tasksCompletedToday: number;
  tasksCompletedThisWeek: number;
  averageTasksPerDay: number;
  mostProductiveDay: string;
  efficiencyScore: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard';
  
  private dashboardDataSignal = signal<DashboardData | null>(null);
  private productivityMetricsSignal = signal<ProductivityMetrics | null>(null);
  private loadingSignal = signal<boolean>(false);
  private errorSignal = signal<string | null>(null);

  public dashboardData = this.dashboardDataSignal.asReadonly();
  public productivityMetrics = this.productivityMetricsSignal.asReadonly();
  public loading = this.loadingSignal.asReadonly();
  public error = this.errorSignal.asReadonly();

  public stats = computed(() => this.dashboardDataSignal()?.stats);
  public recentTasks = computed(() => this.dashboardDataSignal()?.recentTasks || []);
  public upcomingTasks = computed(() => this.dashboardDataSignal()?.upcomingTasks || []);
  public weeklyProgress = computed(() => this.dashboardDataSignal()?.weeklyProgress || []);

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

  loadProductivityMetrics(): Observable<ProductivityMetrics> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.get<ProductivityMetrics>(`${this.apiUrl}/productivity`).pipe(
      tap({
        next: (metrics) => {
          this.productivityMetricsSignal.set(metrics);
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al cargar métricas de productividad');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  loadWeeklyProgress(): Observable<any> {
    return this.http.get(`${this.apiUrl}/weekly-progress`);
  }

  refreshDashboard(): void {
    this.loadDashboardData().subscribe();
    this.loadProductivityMetrics().subscribe();
  }

  clearError(): void {
    this.errorSignal.set(null);
  }

  calculateCompletionRate(completed: number, total: number): number {
    if (total === 0) return 0;
    return Math.round((completed / total) * 100);
  }

  calculateEfficiencyScore(completed: number, total: number, overdue: number): number {
    if (total === 0) return 0;
    
    const completionRate = completed / total;
    const overduePenalty = overdue / total;
    
    return Math.max(0, Math.round((completionRate - overduePenalty) * 100));
  }

  getMostProductiveDay(weeklyData: any[]): string {
    if (!weeklyData || weeklyData.length === 0) return 'N/A';
    
    const mostProductive = weeklyData.reduce((max, current) => 
      current.completed > max.completed ? current : max
    );
    
    return mostProductive.date;
  }
}
