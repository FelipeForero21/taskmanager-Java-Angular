import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface Task {
  taskId: string;
  title: string;
  description?: string;
  taskStatus: {
    taskStatusId: number;
    statusName: string;
    colorHex: string;
  };
  taskPriority: {
    taskPriorityId: number;
    priorityName: string;
    colorHex: string;
  };
  category?: {
    categoryId: number;
    categoryName: string;
    colorHex: string;
  };
  dueDate?: string;
  completedAt?: string;
  estimatedHours?: number;
  createdBy: {
    userId: string;
    firstName: string;
    lastName: string;
  };
  assignedTo?: {
    userId: string;
    firstName: string;
    lastName: string;
  };
}

export interface TaskRequest {
  title: string;
  description?: string;
  taskStatusId: number;
  taskPriorityId: number;
  categoryId?: number;
  assignedToId?: string;
  dueDate?: string;
  estimatedHours?: number;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';
  
  private tasksSignal = signal<Task[]>([]);
  private loadingSignal = signal<boolean>(false);
  private errorSignal = signal<string | null>(null);

  public tasks = this.tasksSignal.asReadonly();
  public loading = this.loadingSignal.asReadonly();
  public error = this.errorSignal.asReadonly();

  public pendingTasks = computed(() => 
    this.tasksSignal().filter(task => task.taskStatus.statusName !== 'Completada')
  );

  public completedTasks = computed(() => 
    this.tasksSignal().filter(task => task.taskStatus.statusName === 'Completada')
  );

  public highPriorityTasks = computed(() => 
    this.tasksSignal().filter(task => task.taskPriority.priorityName === 'Alta')
  );

  public overdueTasks = computed(() => 
    this.tasksSignal().filter(task => {
      if (!task.dueDate || task.taskStatus.statusName === 'Completada') return false;
      return new Date(task.dueDate) < new Date();
    })
  );

  constructor(private http: HttpClient) {}

  loadTasks(): Observable<Task[]> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.get<Task[]>(this.apiUrl).pipe(
      tap({
        next: (tasks) => {
          this.tasksSignal.set(tasks);
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al cargar tareas');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  createTask(taskRequest: TaskRequest): Observable<Task> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.post<Task>(this.apiUrl, taskRequest).pipe(
      tap({
        next: (newTask) => {
          this.tasksSignal.update(tasks => [...tasks, newTask]);
          this.loadingSignal.set(false);
        },
        error: (error) => {
          this.errorSignal.set(error.message || 'Error al crear tarea');
          this.loadingSignal.set(false);
        }
      })
    );
  }

  updateTask(taskId: string, taskRequest: Partial<TaskRequest>): Observable<Task> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.put<Task>(`${this.apiUrl}/${taskId}`, taskRequest).pipe(
      tap({
        next: (updatedTask) => {
          this.tasksSignal.update(tasks => 
            tasks.map(task => task.taskId === taskId ? updatedTask : task)
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

  deleteTask(taskId: string): Observable<void> {
    this.loadingSignal.set(true);
    this.errorSignal.set(null);

    return this.http.delete<void>(`${this.apiUrl}/${taskId}`).pipe(
      tap({
        next: () => {
          this.tasksSignal.update(tasks => 
            tasks.filter(task => task.taskId !== taskId)
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

  getTaskById(taskId: string): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${taskId}`);
  }

  searchTasks(query: string): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/search?q=${query}`);
  }

  filterTasksByStatus(statusId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/filter?statusId=${statusId}`);
  }

  filterTasksByPriority(priorityId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/filter?priorityId=${priorityId}`);
  }

  clearError(): void {
    this.errorSignal.set(null);
  }
}
