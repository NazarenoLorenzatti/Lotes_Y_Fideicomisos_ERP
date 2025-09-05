import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './layout/layout.component';
import { RouterModule } from '@angular/router';
import { SharedModule } from '../shared/shared.module';


@NgModule({
  declarations: [  
    LayoutComponent
  ],
  imports: [
    SharedModule,
    CommonModule,
    RouterModule
  ]
})
export class TemplateModule { }
