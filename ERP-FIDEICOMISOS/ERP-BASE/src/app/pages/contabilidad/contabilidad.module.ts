import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeContabilidadComponent } from './commons/home-contabilidad/home-contabilidad.component';
import { RouterModule } from '@angular/router';
import { ContabilidadRoutingModule } from './contabilidad-routing.module';

@NgModule({
  declarations: [
    HomeContabilidadComponent,
    ],
  exports:[HomeContabilidadComponent],
  imports: [
    CommonModule,
    RouterModule,
    ContabilidadRoutingModule
  ]
})
export class ContabilidadModule { }
