import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { DashboardMainComponent } from './dashboard-main.component';
import { NgApexchartsModule } from 'ng-apexcharts';

@NgModule({
  declarations: [DashboardMainComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatDividerModule,
    MatListModule,
    NgApexchartsModule
  ],
  exports: [DashboardMainComponent]
})
export class DashboardMainModule {} 