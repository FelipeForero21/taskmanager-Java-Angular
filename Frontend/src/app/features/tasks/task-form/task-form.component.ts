import { Component, OnInit, signal, computed, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import { TaskService, TaskRequest, TaskResponse } from '../../../core/task.service';
import { MasterDataService, TaskStatus, TaskPriority, Category } from '../../../core/master-data.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatChipsModule
  ],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.scss'
})
export class TaskFormComponent implements OnInit, OnDestroy {
  loading = signal<boolean>(false);
  isEditMode = signal<boolean>(false);
  taskId = signal<string | null>(null);

  taskStatuses = this.masterDataService.sortedTaskStatuses;
  taskPriorities = this.masterDataService.sortedTaskPriorities;
  categories = this.masterDataService.activeCategories;

  taskForm: FormGroup;
  pageTitle = computed(() => this.isEditMode() ? 'Editar Tarea' : 'Nueva Tarea');
  submitButtonText = computed(() => this.isEditMode() ? 'Actualizar Tarea' : 'Crear Tarea');

  private destroy$ = new Subject<void>();

  constructor(
    private taskService: TaskService,
    private masterDataService: MasterDataService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(200)]],
      description: ['', [Validators.maxLength(1000)]],
      dueDate: [null],
      priorityId: [null, Validators.required],
      statusId: [null, Validators.required],
      categoryId: [null]
    });
  }

  ngOnInit(): void {
    this.masterDataService.loadTaskStatuses().subscribe();
    this.masterDataService.loadTaskPriorities().subscribe();
    this.masterDataService.loadCategories().subscribe();
    this.checkEditMode();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private checkEditMode(): void {
    const taskId = this.route.snapshot.paramMap.get('id');
    if (taskId) {
      this.isEditMode.set(true);
      this.taskId.set(taskId);
      this.loadTaskForEdit(taskId);
    } else {
      this.setDefaultValues();
    }
  }

  private loadTaskForEdit(taskId: string): void {
    this.loading.set(true);
    this.taskService.getTaskById(taskId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (task: TaskResponse) => {
          this.populateForm(task);
          this.loading.set(false);
        },
        error: () => {
          this.showError('Error al cargar la tarea');
          this.loading.set(false);
          this.router.navigate(['/tasks']);
        }
      });
  }

  private populateForm(task: TaskResponse): void {
    this.taskForm.patchValue({
      title: task.title,
      description: task.description || '',
      dueDate: task.dueDate ? new Date(task.dueDate) : null,
      priorityId: task.priority.priorityId,
      statusId: task.status.statusId,
      categoryId: task.category?.categoryId || null
    });
  }

  private setDefaultValues(): void {
    const statuses = this.taskStatuses();
    if (statuses.length > 0) {
      const defaultStatus = statuses.find((s: TaskStatus) => s.statusName === 'Pendiente');
      if (defaultStatus) {
        this.taskForm.patchValue({ statusId: defaultStatus.taskStatusId });
      }
    }
    const priorities = this.taskPriorities();
    if (priorities.length > 0) {
      const defaultPriority = priorities.find((p: TaskPriority) => p.priorityName === 'Media');
      if (defaultPriority) {
        this.taskForm.patchValue({ priorityId: defaultPriority.taskPriorityId });
      }
    }
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      this.loading.set(true);
      const formValue = this.taskForm.value;
      const taskRequest: TaskRequest = {
        title: formValue.title,
        description: formValue.description || undefined,
        dueDate: formValue.dueDate ? formValue.dueDate.toISOString() : undefined,
        priorityId: formValue.priorityId,
        statusId: formValue.statusId,
        categoryId: formValue.categoryId || undefined
      };
      if (this.isEditMode()) {
        this.updateTask(taskRequest);
      } else {
        this.createTask(taskRequest);
      }
    } else {
      this.markFormGroupTouched();
    }
  }

  private createTask(taskRequest: TaskRequest): void {
    this.taskService.createTask(taskRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (task: TaskResponse) => {
          this.showSuccess('Tarea creada exitosamente');
          this.router.navigate(['/tasks', task.taskId]);
        },
        error: () => {
          this.showError('Error al crear la tarea');
          this.loading.set(false);
        }
      });
  }

  private updateTask(taskRequest: TaskRequest): void {
    const taskId = this.taskId();
    if (!taskId) return;
    this.taskService.updateTask(taskId, taskRequest)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (task: TaskResponse) => {
          this.showSuccess('Tarea actualizada exitosamente');
          this.router.navigate(['/tasks', task.taskId]);
        },
        error: () => {
          this.showError('Error al actualizar la tarea');
          this.loading.set(false);
        }
      });
  }

  onCancel(): void {
    this.router.navigate(['/tasks']);
  }

  onClearForm(): void {
    this.taskForm.reset();
    this.setDefaultValues();
  }

  private markFormGroupTouched(): void {
    Object.keys(this.taskForm.controls).forEach(key => {
      const control = this.taskForm.get(key);
      control?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string {
    const field = this.taskForm.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) {
        return 'Este campo es obligatorio';
      }
      if (field.errors['maxlength']) {
        return `Máximo ${field.errors['maxlength'].requiredLength} caracteres`;
      }
    }
    return '';
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.taskForm.get(fieldName);
    return !!(field?.invalid && field.touched);
  }

  getStatusColor(statusName: string): string {
    const status = this.taskStatuses().find(s => s.statusName === statusName);
    return status?.colorHex || '#666666';
  }

  getPriorityColor(priorityName: string): string {
    const priority = this.taskPriorities().find(p => p.priorityName === priorityName);
    return priority?.colorHex || '#666666';
  }

  getCategoryColor(categoryName: string): string {
    const category = this.categories().find(c => c.categoryName === categoryName);
    return category?.colorHex || '#666666';
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
