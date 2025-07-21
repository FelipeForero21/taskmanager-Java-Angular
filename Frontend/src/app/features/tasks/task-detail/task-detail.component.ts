import { Component, OnInit, signal, computed, OnDestroy, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TaskService, TaskResponse } from '../../../core/task.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatTooltipModule
  ],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.scss'
})
export class TaskDetailComponent implements OnInit, OnDestroy {
  task = signal<TaskResponse | null>(null);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  private destroy$ = new Subject<void>();

  constructor(
    private taskService: TaskService,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadTask();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadTask(): void {
    const taskId = this.route.snapshot.paramMap.get('id');
    if (!taskId) {
      this.error.set('ID de tarea no válido');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.taskService.getTaskById(taskId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (task: TaskResponse) => {
          this.task.set(task);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Error al cargar la tarea');
          this.loading.set(false);
        }
      });
  }

  onEditTask(): void {
    const task = this.task();
    if (task) {
      this.router.navigate(['/tasks', 'edit', task.taskId]);
    }
  }

  onDeleteTask(): void {
    const task = this.task();
    if (!task) return;
    const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
      width: '400px',
      data: { taskTitle: task.title }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteTask(task.taskId);
      }
    });
  }

  private deleteTask(taskId: string): void {
    this.taskService.deleteTask(taskId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.showSuccess('Tarea eliminada exitosamente');
          this.router.navigate(['/tasks']);
        },
        error: () => {
          this.showError('Error al eliminar la tarea');
        }
      });
  }

  onBackToList(): void {
    this.router.navigate(['/tasks']);
  }

  getStatusColor(statusName: string): string {
    const task = this.task();
    return task?.status?.colorHex || '#666666';
  }

  getPriorityColor(priorityName: string): string {
    const task = this.task();
    return task?.priority?.colorHex || '#666666';
  }

  getCategoryColor(categoryName: string): string {
    const task = this.task();
    return task?.category?.colorHex || '#666666';
  }

  formatDueDate(dueDate: string): string {
    return this.taskService.formatDueDate(dueDate);
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

@Component({
  selector: 'confirm-delete-dialog',
  template: `
    <h2 mat-dialog-title>Confirmar Eliminación</h2>
    <mat-dialog-content>
      <p>¿Estás seguro de que quieres eliminar la tarea "{{ data.taskTitle }}"?</p>
      <p class="warning">Esta acción no se puede deshacer.</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="true">Eliminar</button>
    </mat-dialog-actions>
  `,
  styles: [`
    .warning {
      color: #f44336;
      font-weight: 500;
    }
  `],
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule
  ]
})
export class ConfirmDeleteDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { taskTitle: string }) {}
} 