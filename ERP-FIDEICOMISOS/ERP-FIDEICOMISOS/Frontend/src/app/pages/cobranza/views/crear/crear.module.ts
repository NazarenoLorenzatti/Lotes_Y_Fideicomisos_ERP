import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CrearReciboComponent } from './crear-recibo/crear-recibo.component';
import { CrearReciboRoutingModule } from './crear-routing.module';
import { SharedModule } from '../../../../shared/shared.module';
import { CuentaCorrienteComponent } from '../../../contactos/views/contacto/cuenta-corriente/cuenta-corriente.component';
import { ViewsModule } from '../../../contactos/views/views.module';



@NgModule({
  declarations: [
    CrearReciboComponent
  ],
  exports: [
    CrearReciboComponent
  ],
  imports: [
    CommonModule,
    CrearReciboRoutingModule,
    SharedModule,
    ViewsModule
  ]
})
export class CrearModule { }
