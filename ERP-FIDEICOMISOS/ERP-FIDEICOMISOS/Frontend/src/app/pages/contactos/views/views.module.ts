import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContactoComponent } from './contacto/contacto.component';
import { CrearComponent } from './crear/crear.component';
import { ViewsRoutingModule } from './views-routing.module';
import { RouterModule } from '@angular/router';
import { CuentaCorrienteComponent } from './contacto/cuenta-corriente/cuenta-corriente.component';
import { SharedModule } from '../../../shared/shared.module';

@NgModule({
  declarations: [
    ContactoComponent,
    CrearComponent,
    CuentaCorrienteComponent
  ],
  exports: [
    CuentaCorrienteComponent
  ],
  imports: [
    ViewsRoutingModule,
    RouterModule,
    CommonModule,
    SharedModule
  ]
})
export class ViewsModule { }
