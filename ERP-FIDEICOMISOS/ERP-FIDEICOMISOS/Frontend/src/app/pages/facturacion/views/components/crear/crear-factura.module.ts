import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CrearFacturaComponent } from './crear-factura/crear-factura.component';
import { SharedModule } from '../../../../../shared/shared.module';
import { CrearFacturaRoutingModule } from './crear-factura-routing.module';

@NgModule({
  declarations: [
    CrearFacturaComponent
  ],
  exports:[ 
    CrearFacturaComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    CrearFacturaRoutingModule
  ]
})
export class CrearFacturaModule { }
