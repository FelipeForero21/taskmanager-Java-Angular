import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { TaskListComponent } from './task-list/task-list.component';
import { TaskFormComponent } from './task-form/task-form.component';
import { TaskDetailComponent } from './task-detail/task-detail.component';

const routes: Routes = [
  { path: '', component: TaskListComponent },
  { path: 'create', component: TaskFormComponent },
  { path: ':id', component: TaskDetailComponent },
  { path: 'edit/:id', component: TaskFormComponent }
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ]
})
export class TasksModule { }
