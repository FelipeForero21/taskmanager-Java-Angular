import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { DashboardMainComponent } from './dashboard-main/dashboard-main.component';
import { DashboardMainModule } from './dashboard-main/dashboard-main.module';

const routes: Routes = [
  { path: '', component: DashboardMainComponent }
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    DashboardMainModule
  ]
})
export class DashboardModule { }
