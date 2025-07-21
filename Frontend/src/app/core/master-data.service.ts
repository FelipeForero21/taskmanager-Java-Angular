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

  loadMasterData(): Observable<MasterData> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.get<MasterData>(this.apiUrl).pipe(
      tap({
        next: (data) => {
          this.taskStatusesSignal.set(data.taskStatuses);
          this.taskPrioritiesSignal.set(data.taskPriorities);
          this.categoriesSignal.set(data.categories);
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al cargar datos maestros');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  loadTaskStatuses(): Observable<TaskStatus[]> {
    return this.http.get<TaskStatus[]>(`${this.apiUrl}/task-statuses`).pipe(
      tap(statuses => this.taskStatusesSignal.set(statuses))
    );
  }

  loadTaskPriorities(): Observable<TaskPriority[]> {
    return this.http.get<TaskPriority[]>(`${this.apiUrl}/task-priorities`).pipe(
      tap(priorities => this.taskPrioritiesSignal.set(priorities))
    );
  }

  loadCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`).pipe(
      tap(categories => this.categoriesSignal.set(categories))
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

  createCategory(category: Omit<Category, 'categoryId'>): Observable<Category> {
    return this.http.post<Category>(`${this.apiUrl}/categories`, category).pipe(
      tap(newCategory => {
        this.categoriesSignal.update(categories => [...categories, newCategory]);
      })
    );
  }

  clearError(): void {
    this.errorSignal.set(null);
  }
}
