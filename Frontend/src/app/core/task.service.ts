import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';

// DTOs alineados al backend
export interface TaskRequest {
  title: string;
  description?: string;
  dueDate?: string;
  priorityId: number;
  statusId: number;
  categoryId?: number;
  assignedTo?: string;
}

export interface TaskResponse {
  taskId: string;
  title: string;
  description?: string;
  dueDate?: string;
  createdAt: string;
  updatedAt: string;
  isActive: boolean;
  priority: PriorityInfo;
  status: StatusInfo;
  category?: CategoryInfo;
  assignedTo?: UserInfo;
  createdBy: UserInfo;
  isOverdue: boolean;
  daysUntilDue: number;
}

export interface PriorityInfo {
  priorityId: number;
  priorityName: string;
  priorityLevel: number;
  colorHex: string;
}

export interface StatusInfo {
  statusId: number;
  statusName: string;
  colorHex: string;
}

export interface CategoryInfo {
  categoryId: number;
  categoryName: string;
  colorHex: string;
  iconName?: string;
}

export interface UserInfo {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface TaskFilters {
  statusId?: number;
  priorityId?: number;
  categoryId?: number;
  searchTerm?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
  startDate?: string;
  endDate?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  // Signals para el listado, loading, error, paginación y filtros
  private tasksSignal = signal<TaskResponse[]>([]);
  private loadingSignal = signal<boolean>(false);
  private errorSignal = signal<string | null>(null);
  private totalElementsSignal = signal<number>(0);
  private pageSignal = signal<number>(0);
  private pageSizeSignal = signal<number>(10);
  private filtersSignal = signal<TaskFilters>({});

  public tasks = this.tasksSignal.asReadonly();
  public loading = this.loadingSignal.asReadonly();
  public error = this.errorSignal.asReadonly();
  public totalElements = this.totalElementsSignal.asReadonly();
  public page = this.pageSignal.asReadonly();
  public pageSize = this.pageSizeSignal.asReadonly();
  public filters = this.filtersSignal.asReadonly();

  constructor(private http: HttpClient) {}

  // Cargar tareas con paginación y filtros
  loadTasks(filters?: TaskFilters): Observable<PageResponse<TaskResponse>> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);
    const mergedFilters = { ...this.filtersSignal(), ...filters };
    this.filtersSignal.set(mergedFilters);
    let params = new HttpParams()
      .set('page', (mergedFilters.page ?? this.pageSignal() ?? 0).toString())
      .set('size', (mergedFilters.size ?? this.pageSizeSignal() ?? 10).toString())
      .set('sortBy', mergedFilters.sortBy ?? 'createdAt')
      .set('sortDir', mergedFilters.sortDir ?? 'desc');
    if (mergedFilters.statusId) params = params.set('statusId', mergedFilters.statusId.toString());
    if (mergedFilters.priorityId) params = params.set('priorityId', mergedFilters.priorityId.toString());
    if (mergedFilters.categoryId) params = params.set('categoryId', mergedFilters.categoryId.toString());
    if (mergedFilters.searchTerm) params = params.set('searchTerm', mergedFilters.searchTerm);
    if (mergedFilters.startDate) params = params.set('startDate', mergedFilters.startDate);
    if (mergedFilters.endDate) params = params.set('endDate', mergedFilters.endDate);
    return this.http.get<PageResponse<TaskResponse>>(this.apiUrl, { params }).pipe(
      tap({
        next: (response) => {
          this.tasksSignal.set(response.content);
          this.totalElementsSignal.set(response.totalElements);
          this.pageSignal.set(response.number);
          this.pageSizeSignal.set(response.size);
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al cargar tareas');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  createTask(task: TaskRequest): Observable<TaskResponse> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);
    return this.http.post<TaskResponse>(this.apiUrl, task).pipe(
      tap({
        next: (newTask) => {
          this.tasksSignal.update(tasks => [newTask, ...tasks]);
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al crear tarea');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  updateTask(taskId: string, task: TaskRequest): Observable<TaskResponse> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);
    return this.http.put<TaskResponse>(`${this.apiUrl}/${taskId}`, task).pipe(
      tap({
        next: (updatedTask) => {
          this.tasksSignal.update(tasks =>
            tasks.map(t => t.taskId === taskId ? updatedTask : t)
          );
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al actualizar tarea');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  deleteTask(taskId: string): Observable<any> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);
    return this.http.delete(`${this.apiUrl}/${taskId}`).pipe(
      tap({
        next: () => {
          this.tasksSignal.update(tasks =>
            tasks.filter(t => t.taskId !== taskId)
          );
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al eliminar tarea');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  getTaskById(taskId: string): Observable<TaskResponse> {
    return this.http.get<TaskResponse>(`${this.apiUrl}/${taskId}`);
  }

  // Métodos utilitarios para fechas
  formatDueDate(dueDate: string): string {
    if (!dueDate) return '';
    const date = new Date(dueDate);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getDaysUntilDue(dueDate: string): number {
    if (!dueDate) return 0;
    const due = new Date(dueDate);
    const now = new Date();
    const diffTime = due.getTime() - now.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  isOverdue(dueDate: string): boolean {
    if (!dueDate) return false;
    return new Date(dueDate) < new Date();
  }

  isDueSoon(dueDate: string): boolean {
    if (!dueDate) return false;
    const daysUntilDue = this.getDaysUntilDue(dueDate);
    return daysUntilDue >= 0 && daysUntilDue <= 7;
  }

  clearError(): void {
    this.errorSignal.set(null);
  }

  clearFilters(): void {
    this.filtersSignal.set({});
  }
}
