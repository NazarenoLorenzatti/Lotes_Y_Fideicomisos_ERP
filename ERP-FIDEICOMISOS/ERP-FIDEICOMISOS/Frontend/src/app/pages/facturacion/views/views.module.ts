import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ViewsRoutingModule } from './views-routing.module';
import { ComprobanteComponent } from './components/comprobante/comprobante.component';
import { PendientesComponent } from './components/pendientes/pendientes.component';
import { SharedModule } from '../../../shared/shared.module';
import { ListPendientesComponent } from './components/pendientes/list/list-pendientes.component';
import { PreComprobanteComponent } from './components/pre-comprobante/pre-comprobante.component';
import { PreComprobanteCommonComponent } from './components/pre-comprobante/pre-comprobante-common/pre-comprobante-common.component';
import { CrearFacturaModule } from './components/crear/crear-factura.module';

@NgModule({
  declarations: [
    ComprobanteComponent,
    PendientesComponent,
    ListPendientesComponent,
    PreComprobanteComponent,
    PreComprobanteCommonComponent,
  ],
  imports: [
    CommonModule,
    ViewsRoutingModule,
    SharedModule,
    CrearFacturaModule
]
})
export class ViewsModule { }
