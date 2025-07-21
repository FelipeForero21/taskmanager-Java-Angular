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
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TaskService, TaskResponse, TaskFilters, PageResponse } from '../../../core/task.service';
import { MasterDataService, TaskStatus, TaskPriority, Category } from '../../../core/master-data.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDeleteDialog } from '../task-detail/task-detail.component';

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
    ReactiveFormsModule
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
      categoryId: ['']
    });
  }

  ngOnInit(): void {
    this.masterDataService.loadTaskStatuses().subscribe();
    this.masterDataService.loadTaskPriorities().subscribe();
    this.masterDataService.loadCategories().subscribe();
    this.loadTasks();
    this.filterForm.valueChanges.subscribe(() => {
      this.onFilterChange();
    });
  }

  loadTasks(page: number = 0, size: number = 10): void {
    const filters: TaskFilters = {
      ...this.filterForm.value,
      page,
      size
    };
    this.taskService.loadTasks(filters).subscribe();
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
    this.loadTasks(0, this.pageSize());
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
