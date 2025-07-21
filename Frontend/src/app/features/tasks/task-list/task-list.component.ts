import { Component, OnInit } from '@angular/core';
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
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { TaskService, Task } from '../../../core/task.service';
import { MasterDataService } from '../../../core/master-data.service';

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
    ReactiveFormsModule
  ],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent implements OnInit {
  filterForm: FormGroup;

  constructor(
    private taskService: TaskService,
    private masterDataService: MasterDataService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      status: [''],
      priority: [''],
      category: ['']
    });
  }

  ngOnInit(): void {
    this.loadData();
    this.setupFilterListener();
  }

  loadData(): void {
    this.taskService.loadTasks().subscribe();
    this.masterDataService.loadMasterData().subscribe();
  }

  setupFilterListener(): void {
    this.filterForm.valueChanges.subscribe(filters => {
      console.log('Filtros aplicados:', filters);
    });
  }

  get tasks() {
    return this.taskService.tasks();
  }

  get loading() {
    return this.taskService.loading();
  }

  get error() {
    return this.taskService.error();
  }

  get taskStatuses() {
    return this.masterDataService.sortedTaskStatuses();
  }

  get taskPriorities() {
    return this.masterDataService.sortedTaskPriorities();
  }

  get categories() {
    return this.masterDataService.activeCategories();
  }

  createTask(): void {
    this.router.navigate(['/tasks/create']);
  }

  editTask(taskId: string): void {
    this.router.navigate(['/tasks/edit', taskId]);
  }

  deleteTask(taskId: string): void {
    if (confirm('¿Estás seguro de que quieres eliminar esta tarea?')) {
      this.taskService.deleteTask(taskId).subscribe();
    }
  }

  getPriorityColor(priority: string): string {
    switch (priority.toLowerCase()) {
      case 'alta': return 'warn';
      case 'media': return 'accent';
      case 'baja': return 'primary';
      default: return 'primary';
    }
  }

  getStatusColor(status: string): string {
    switch (status.toLowerCase()) {
      case 'completada': return 'primary';
      case 'en progreso': return 'accent';
      case 'pendiente': return 'warn';
      default: return 'primary';
    }
  }

  isOverdue(task: Task): boolean {
    if (!task.dueDate || task.taskStatus.statusName === 'Completada') return false;
    return new Date(task.dueDate) < new Date();
  }

  getDaysUntilDue(task: Task): number {
    if (!task.dueDate) return 0;
    const dueDate = new Date(task.dueDate);
    const today = new Date();
    const diffTime = dueDate.getTime() - today.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  clearFilters(): void {
    this.filterForm.reset();
  }
}
