import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CrearComponent } from './components/crear/crear.component';
import { ListarComponent } from './components/listar/listar.component';
import { DetallesComponent } from './components/detalles/detalles.component';
import { CuentasRoutingModule } from './cuentas-routing.module';
import { SharedModule } from '../../../shared/shared.module';



@NgModule({
  declarations: [
    CrearComponent,
    ListarComponent,
    DetallesComponent
  ],
  imports: [
    CommonModule,
    CuentasRoutingModule,
    SharedModule
  ]
})
export class CuentasContablesModule { }
