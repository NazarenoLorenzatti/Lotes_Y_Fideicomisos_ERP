import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FacturaComponent } from './factura/factura.component';
import { OrdenPagoComponent } from './orden-pago/orden-pago.component';
import { MinutaPagoComponent } from './minuta-pago/minuta-pago.component';
import { PagosRoutingModule } from './pagos-routing.module';
import { InicioComponent } from './inicio/inicio.component';



@NgModule({
  declarations: [
    FacturaComponent,
    OrdenPagoComponent,
    MinutaPagoComponent,
    InicioComponent
  ],
  imports: [
    PagosRoutingModule,
    CommonModule
  ]
})
export class PagosModule { }
