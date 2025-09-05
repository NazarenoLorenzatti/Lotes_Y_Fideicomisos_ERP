import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KpisComponent } from './commons/kpis/kpis.component';
import { ListaFacturasComponent } from './commons/lista-facturas/lista-facturas.component';
import { ProveedoresRoutingModule } from './proveedores-routing.module';



@NgModule({
  declarations: [
    KpisComponent,
    ListaFacturasComponent
  ],
  imports: [
    ProveedoresRoutingModule,
    CommonModule
  ]
})
export class ProveedoresModule { }
