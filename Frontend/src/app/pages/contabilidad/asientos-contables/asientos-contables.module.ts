import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CrearComponent } from './components/crear/crear.component';
import { DetallesAsientoComponent } from './components/detalles-asiento/detalles-asiento.component';
import { AsientosRoutingModule } from './asientos-routing.module';
import { SharedModule } from '../../../shared/shared.module';
import { ConciliacionComponent } from './components/detalles-asiento/conciliacion/conciliacion.component';
import { TablaComponent } from './components/tabla/tabla.component';
import { ListasComponent } from './components/tabla/listas/listas.component';


@NgModule({
  declarations: [

    CrearComponent,
    DetallesAsientoComponent,
    ConciliacionComponent,
    TablaComponent,
    ListasComponent
  ],
  imports: [
    CommonModule,
    AsientosRoutingModule,
    SharedModule
  ]
})
export class AsientosContablesModule { }
