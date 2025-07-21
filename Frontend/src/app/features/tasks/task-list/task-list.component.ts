import { Component, OnInit, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TaskService, TaskResponse, TaskFilters, PageResponse } from '../../../core/task.service';
import { MasterDataService, TaskStatus, TaskPriority, Category } from '../../../core/master-data.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDeleteDialog } from '../task-detail/task-detail.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatMenuModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent implements OnInit {
  filterForm: FormGroup;
  displayedColumns = ['title', 'status', 'priority', 'category', 'dueDate', 'assignedTo', 'actions'];

  // Signals para paginación y filtros
  tasks = this.taskService.tasks;
  loading = this.taskService.loading;
  error = this.taskService.error;
  totalElements = this.taskService.totalElements;
  page = this.taskService.page;
  pageSize = this.taskService.pageSize;

  taskStatuses = this.masterDataService.sortedTaskStatuses;
  taskPriorities = this.masterDataService.sortedTaskPriorities;
  categories = this.masterDataService.activeCategories;

  constructor(
    private taskService: TaskService,
    private masterDataService: MasterDataService,
    private router: Router,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.filterForm = this.fb.group({
      searchTerm: [''],
      statusId: [''],
      priorityId: [''],
      categoryId: [''],
      startDate: [null],
      endDate: [null]
    });
  }

  ngOnInit(): void {
    this.masterDataService.loadTaskStatuses().subscribe();
    this.masterDataService.loadTaskPriorities().subscribe();
    this.masterDataService.loadCategories().subscribe();
    this.loadTasks();
    // Normalizar fechas al iniciar
    this.normalizeDateFields();
    this.filterForm.valueChanges.subscribe(() => {
      this.normalizeDateFields();
      this.onFilterChange();
    });
  }

  private normalizeDateFields(): void {
    const startDate = this.filterForm.get('startDate')?.value;
    if (startDate && typeof startDate === 'string') {
      this.filterForm.get('startDate')?.setValue(new Date(startDate), { emitEvent: false });
    }
    const endDate = this.filterForm.get('endDate')?.value;
    if (endDate && typeof endDate === 'string') {
      this.filterForm.get('endDate')?.setValue(new Date(endDate), { emitEvent: false });
    }
  }

  loadTasks(page: number = 0, size: number = 10): void {
    const rawFilters = this.filterForm.value;
    // Convertir fechas a ISO string y renombrar para fecha límite
    const cleanedFilters: any = { ...rawFilters, page, size };
    if (cleanedFilters.startDate instanceof Date) {
      cleanedFilters.startDate = cleanedFilters.startDate.toISOString();
    }
    if (cleanedFilters.endDate instanceof Date) {
      cleanedFilters.endDate = cleanedFilters.endDate.toISOString();
    }
    // Limpiar filtros vacíos
    Object.keys(cleanedFilters).forEach(key => {
      if (
        cleanedFilters[key] === '' ||
        cleanedFilters[key] === null ||
        cleanedFilters[key] === undefined
      ) {
        delete cleanedFilters[key];
      }
    });
    // Si no hay filtros, solo enviar paginación
    const onlyPagination = Object.keys(cleanedFilters).every(key => ['page', 'size'].includes(key));
    if (onlyPagination) {
      this.taskService.loadTasks({ page, size }).subscribe();
    } else {
      this.taskService.loadTasks(cleanedFilters as TaskFilters).subscribe();
    }
  }

  onPageChange(event: PageEvent): void {
    this.loadTasks(event.pageIndex, event.pageSize);
  }

  onFilterChange(): void {
    this.loadTasks(0, this.pageSize());
  }

  onCreateTask(): void {
    this.router.navigate(['/tasks/create']);
  }

  onViewTask(task: TaskResponse): void {
    this.router.navigate(['/tasks', task.taskId]);
  }

  onEditTask(task: TaskResponse): void {
    this.router.navigate(['/tasks', 'edit', task.taskId]);
  }

  onDeleteTask(task: TaskResponse): void {
    const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
      width: '400px',
      data: { taskTitle: task.title }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.taskService.deleteTask(task.taskId).subscribe({
          next: () => this.showSuccess('Tarea eliminada exitosamente'),
          error: () => this.showError('Error al eliminar tarea')
        });
      }
    });
  }

  onClearFilters(): void {
    this.filterForm.reset();
    this.taskService.clearFilters();
    this.taskService.loadTasks({ page: 0, size: this.pageSize() }).subscribe();
  }

  getStatusColor(status: string): string {
    const statusObj = this.taskStatuses().find(s => s.statusName === status);
    return statusObj?.colorHex || '#666666';
  }

  getPriorityColor(priority: string): string {
    const priorityObj = this.taskPriorities().find(p => p.priorityName === priority);
    return priorityObj?.colorHex || '#666666';
  }

  getCategoryColor(category: string): string {
    const categoryObj = this.categories().find(c => c.categoryName === category);
    return categoryObj?.colorHex || '#666666';
  }

  formatDueDate(dueDate: string): string {
    return this.taskService.formatDueDate(dueDate);
  }

  getDaysUntilDue(dueDate: string): number {
    return this.taskService.getDaysUntilDue(dueDate);
  }

  isOverdue(dueDate: string): boolean {
    return this.taskService.isOverdue(dueDate);
  }

  isDueSoon(dueDate: string): boolean {
    return this.taskService.isDueSoon(dueDate);
  }

  getDueDateClass(dueDate: string): string {
    if (!dueDate) return '';
    if (this.isOverdue(dueDate)) return 'overdue';
    if (this.isDueSoon(dueDate)) return 'due-soon';
    return '';
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Cerrar', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Cerrar', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['error-snackbar']
    });
  }
}
