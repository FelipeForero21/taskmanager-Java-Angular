import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface TaskStatus {
  taskStatusId: number;
  statusName: string;
  statusDescription?: string;
  colorHex: string;
  sortOrder: number;
  isActive: boolean;
}

export interface TaskPriority {
  taskPriorityId: number;
  priorityName: string;
  priorityDescription?: string;
  colorHex: string;
  sortOrder: number;
  isActive: boolean;
}

export interface Category {
  categoryId: number;
  categoryName: string;
  categoryDescription?: string;
  colorHex: string;
  iconName?: string;
  isActive: boolean;
}

export interface MasterData {
  taskStatuses: TaskStatus[];
  taskPriorities: TaskPriority[];
  categories: Category[];
}

@Injectable({
  providedIn: 'root'
})
export class MasterDataService {
  private apiUrl = 'http://localhost:8080/api/master-data';
  
  private taskStatusesSignal = signal<TaskStatus[]>([]);
  private taskPrioritiesSignal = signal<TaskPriority[]>([]);
  private categoriesSignal = signal<Category[]>([]);
  private loadingSignal = signal<boolean>(false);
  private errorSignal = signal<string | null>(null);

  public taskStatuses = this.taskStatusesSignal.asReadonly();
  public taskPriorities = this.taskPrioritiesSignal.asReadonly();
  public categories = this.categoriesSignal.asReadonly();
  public loading = this.loadingSignal.asReadonly();
  public error = this.errorSignal.asReadonly();

  public activeTaskStatuses = computed(() => 
    this.taskStatusesSignal().filter(status => status.isActive)
  );

  public activeTaskPriorities = computed(() => 
    this.taskPrioritiesSignal().filter(priority => priority.isActive)
  );

  public activeCategories = computed(() => 
    this.categoriesSignal().filter(category => category.isActive)
  );

  public sortedTaskStatuses = computed(() => 
    this.activeTaskStatuses().sort((a, b) => a.sortOrder - b.sortOrder)
  );

  public sortedTaskPriorities = computed(() => 
    this.activeTaskPriorities().sort((a, b) => a.sortOrder - b.sortOrder)
  );

  constructor(private http: HttpClient) {}

  loadTaskStatuses(): Observable<TaskStatus[]> {
    this.loadingSignal.set(true);
    return this.http.get<TaskStatus[]>(`${this.apiUrl}/statuses`).pipe(
      tap({
        next: statuses => {
          this.taskStatusesSignal.set(statuses);
          this.loadingSignal.set(false);
        },
        error: () => {
          this.loadingSignal.set(false);
        }
      })
    );
  }

  loadTaskPriorities(): Observable<TaskPriority[]> {
    this.loadingSignal.set(true);
    return this.http.get<TaskPriority[]>(`${this.apiUrl}/priorities`).pipe(
      tap({
        next: priorities => {
          this.taskPrioritiesSignal.set(priorities);
          this.loadingSignal.set(false);
        },
        error: () => {
          this.loadingSignal.set(false);
        }
      })
    );
  }

  loadCategories(): Observable<Category[]> {
    this.loadingSignal.set(true);
    return this.http.get<Category[]>("http://localhost:8080/api/categories").pipe(
      tap({
        next: categories => {
          this.categoriesSignal.set(categories);
          this.loadingSignal.set(false);
        },
        error: () => {
          this.loadingSignal.set(false);
        }
      })
    );
  }

  getTaskStatusById(statusId: number): TaskStatus | undefined {
    return this.taskStatusesSignal().find(status => status.taskStatusId === statusId);
  }

  getTaskPriorityById(priorityId: number): TaskPriority | undefined {
    return this.taskPrioritiesSignal().find(priority => priority.taskPriorityId === priorityId);
  }

  getCategoryById(categoryId: number): Category | undefined {
    return this.categoriesSignal().find(category => category.categoryId === categoryId);
  }

  createTaskStatus(status: Omit<TaskStatus, 'taskStatusId'>): Observable<TaskStatus> {
    return this.http.post<TaskStatus>(`${this.apiUrl}/task-statuses`, status).pipe(
      tap(newStatus => {
        this.taskStatusesSignal.update(statuses => [...statuses, newStatus]);
      })
    );
  }

  createTaskPriority(priority: Omit<TaskPriority, 'taskPriorityId'>): Observable<TaskPriority> {
    return this.http.post<TaskPriority>(`${this.apiUrl}/task-priorities`, priority).pipe(
      tap(newPriority => {
        this.taskPrioritiesSignal.update(priorities => [...priorities, newPriority]);
      })
    );
  }



  clearError(): void {
    this.errorSignal.set(null);
  }
}
